package com.wodox.chat.ui.channel

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.wodox.core.base.viewmodel.BaseUiStateViewModel
import com.wodox.domain.chat.model.Channel
import com.wodox.domain.chat.usecase.channel.CreateChannelUseCase
import com.wodox.domain.chat.usecase.channel.DeleteChannelUseCase
import com.wodox.domain.chat.usecase.channel.GetAllChannelsByIdUseCase
import com.wodox.domain.chat.usecase.channel.GetAllChannelsUseCase
import com.wodox.domain.chat.usecase.channel.GetJoinedChannelsUseCase
import com.wodox.domain.chat.usecase.channel.GetMyChannelsUseCase
import com.wodox.domain.chat.usecase.channel.JoinChannelParams
import com.wodox.domain.chat.usecase.channel.JoinChannelUseCase
import com.wodox.domain.chat.usecase.channel.RemoveChannelMemberParams
import com.wodox.domain.chat.usecase.channel.RemoveChannelMemberUseCase
import com.wodox.domain.user.usecase.GetCurrentUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class ChannelListViewModel @Inject constructor(
    app: Application,
    private val getAllChannelsUseCase: GetAllChannelsUseCase,
    private val getJoinedChannelsUseCase: GetJoinedChannelsUseCase,
    private val getMyChannelsUseCase: GetMyChannelsUseCase,
    private val createChannelUseCase: CreateChannelUseCase,
    private val deleteChannelUseCase: DeleteChannelUseCase,
    private val joinChannelUseCase: JoinChannelUseCase,
    private val removeChannelMemberUseCase: RemoveChannelMemberUseCase,
    private val getCurrentUser: GetCurrentUser,
) : BaseUiStateViewModel<ChannelListUiState, ChannelListUiEvent, ChannelListUiAction>(app) {

    override fun initialState(): ChannelListUiState = ChannelListUiState()


    private val currentMode = MutableStateFlow(ViewMode.ALL)
    private var collectionJob: Job? = null
    private var joinedChannelsJob: Job? = null

    init {
        loadCurrentUser()
        observeChannels()
    }

    override fun handleAction(action: ChannelListUiAction) {
        when (action) {
            is ChannelListUiAction.LoadAllChannels -> {
                currentMode.value = ViewMode.ALL
            }

            is ChannelListUiAction.LoadJoinedChannels -> {
                currentMode.value = ViewMode.JOINED
            }

            is ChannelListUiAction.LoadMyChannels -> {
                currentMode.value = ViewMode.MY_CHANNELS
            }

            is ChannelListUiAction.CreateChannel -> createChannel(
                action.name,
                action.description,
                action.isPrivate
            )

            is ChannelListUiAction.SearchChannels -> searchChannels(action.query)
            is ChannelListUiAction.DeleteChannel -> deleteChannel(action.channelId)
            is ChannelListUiAction.JoinChannel -> joinChannel(action.channelId)
            is ChannelListUiAction.LeaveChannel -> leaveChannel(action.channelId)
        }
    }


    private fun loadCurrentUser() {
        viewModelScope.launch(Dispatchers.IO) {
            val user = getCurrentUser() ?: return@launch

            android.util.Log.d("ChannelListVM", "✅ Current user loaded: ${user.id}")
            updateState { it.copy(currentUser = user) }
            observeJoinedChannels(user.id)
        }
    }


    private fun observeJoinedChannels(userId: UUID) {
        joinedChannelsJob?.cancel()
        joinedChannelsJob = viewModelScope.launch(Dispatchers.IO) {
            getJoinedChannelsUseCase(userId)
                .catch { exception ->
                    android.util.Log.e("ChannelListVM", "Error loading user channels", exception)
                    emit(emptyList())
                }
                .collectLatest { channels ->
                    android.util.Log.d("ChannelListVM", "📦 User joined ${channels.size} channels")
                    updateState { it.copy(channelsJoin = channels) }
                }
        }
    }

    private fun observeChannels() {
        collectionJob?.cancel()
        collectionJob = viewModelScope.launch(Dispatchers.IO) {
            combine(
                uiState.map { it.currentUser },
                currentMode
            ) { user, mode ->
                user to mode
            }
                .flatMapLatest { (user, mode) ->
                    if (user == null) {
                        android.util.Log.d("ChannelListVM", "⏳ Waiting for user to load...")
                        return@flatMapLatest flowOf(emptyList())
                    }

                    updateState { it.copy(isLoading = true, error = null, searchQuery = "") }

                    android.util.Log.d(
                        "ChannelListVM",
                        "📋 Loading channels - Mode: $mode, User: ${user.id}"
                    )

                    when (mode) {
                        ViewMode.ALL -> {
                            getAllChannelsUseCase()
                        }

                        ViewMode.JOINED -> {
                            android.util.Log.d(
                                "ChannelListVM",
                                "Loading joined channels for user: ${user.id}"
                            )
                            getJoinedChannelsUseCase(user.id)
                        }

                        ViewMode.MY_CHANNELS -> {
                            android.util.Log.d(
                                "ChannelListVM",
                                "Loading my channels for user: ${user.id}"
                            )
                            getMyChannelsUseCase(user.id)
                        }
                    }
                }
                .catch { exception ->
                    android.util.Log.e("ChannelListVM", "❌ Error loading channels", exception)
                    handleError("Failed to load channels", exception)
                    emit(emptyList())
                }
                .collectLatest { channels ->
                    android.util.Log.d("ChannelListVM", "✅ Received ${channels.size} channels")
                    channels.forEachIndexed { index, channel ->
                        android.util.Log.d(
                            "ChannelListVM",
                            "  [$index] ${channel.name} (joined=${channel.isJoined})"
                        )
                    }

                    updateState {
                        it.copy(
                            isLoading = false,
                            channels = channels,
                            error = null
                        )
                    }
                }
        }
    }

    private fun createChannel(name: String, description: String?, isPrivate: Boolean) {
        val currentUser = uiState.value.currentUser ?: return

        viewModelScope.launch(Dispatchers.IO) {
            updateState { it.copy(isLoading = true, error = null) }
            val newChannel = Channel(
                name = name,
                description = description,
                creatorId = currentUser.id,
                isPrivate = isPrivate,
                memberCount = 1
            )

            val createdChannel = createChannelUseCase(newChannel)

            updateState { it.copy(isLoading = false, error = null) }

            withContext(Dispatchers.Main) {
                sendEvent(ChannelListUiEvent.ChannelCreated(createdChannel))
            }
        }
    }

    private fun deleteChannel(channelId: UUID) {
        viewModelScope.launch(Dispatchers.IO) {
            updateState { it.copy(isLoading = true, error = null) }

            deleteChannelUseCase(channelId)
            updateState {
                it.copy(
                    isLoading = false,
                    channels = it.channels.filterNot { channel ->
                        channel.id == channelId
                    },
                    error = null
                )
            }
        }
    }

    private fun searchChannels(query: String) {
        if (query.isBlank()) {
            currentMode.value = currentMode.value
            return
        }
        val filtered = uiState.value.channels.filter {
            it.name.contains(query, ignoreCase = true) ||
                    it.description?.contains(query, ignoreCase = true) == true
        }

        updateState {
            it.copy(
                channels = filtered,
                searchQuery = query
            )
        }
    }

    private fun handleError(message: String, exception: Throwable) {
        val errorMsg = "$message: ${exception.message}"
        updateState { it.copy(isLoading = false, error = errorMsg) }
        sendEvent(ChannelListUiEvent.Error(errorMsg))
        android.util.Log.e("ChannelListViewModel", errorMsg, exception)
    }

    private fun joinChannel(channelId: UUID) {
        val currentUser = uiState.value.currentUser ?: return
        viewModelScope.launch(Dispatchers.IO) {
            val channel = uiState.value.channels.find { it.id == channelId }
            if (channel?.isJoined == true) {
                android.util.Log.w("ChannelListVM", "⚠️ Already joined channel: $channelId")
                withContext(Dispatchers.Main) {
                    sendEvent(ChannelListUiEvent.Error("You have already joined this channel"))
                }
                return@launch
            }
            android.util.Log.d("ChannelListVM", "🔗 Joining channel: $channelId")
            updateChannelMemberCount(channelId, increment = true)
            val params = JoinChannelParams(
                channelId = channelId,
                userId = currentUser.id
            )
            joinChannelUseCase(params)
            withContext(Dispatchers.Main) {
                sendEvent(ChannelListUiEvent.ChannelJoined(channelId))
            }
        }
    }

    private fun leaveChannel(channelId: UUID) {
        val currentUser = uiState.value.currentUser ?: return

        viewModelScope.launch(Dispatchers.IO) {
            updateChannelMemberCount(channelId, increment = false)

            val params = RemoveChannelMemberParams(
                channelId = channelId,
                userId = currentUser.id
            )
            removeChannelMemberUseCase(params)
            withContext(Dispatchers.Main) {
                sendEvent(ChannelListUiEvent.ChannelLeft(channelId))
            }

        }
    }


    private fun updateChannelMemberCount(channelId: UUID, increment: Boolean) {
        updateState { state ->
            state.copy(
                channels = state.channels.map { channel ->
                    if (channel.id == channelId) {
                        channel.copy(
                            memberCount = if (increment) {
                                channel.memberCount + 1
                            } else {
                                maxOf(0, channel.memberCount - 1)
                            },
                            isJoined = increment
                        )
                    } else {
                        channel
                    }
                }
            )
        }
    }

    private enum class ViewMode {
        ALL, JOINED, MY_CHANNELS
    }
}