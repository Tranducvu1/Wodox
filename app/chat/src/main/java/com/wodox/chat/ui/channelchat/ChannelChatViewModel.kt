package com.wodox.chat.ui.channelchat

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.wodox.core.base.viewmodel.BaseUiStateViewModel
import com.wodox.domain.chat.model.ChannelMessage
import com.wodox.domain.chat.usecase.channel.ClearUnreadCountUseCase
import com.wodox.domain.chat.usecase.channel.GetChannelByIdUseCase
import com.wodox.domain.chat.usecase.channel.GetChannelMembersUseCase
import com.wodox.domain.chat.usecase.channel.GetChannelMessagesUseCase
import com.wodox.domain.chat.usecase.channel.SendChannelMessageUseCase
import com.wodox.domain.user.usecase.GetCurrentUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class ChannelChatViewModel @Inject constructor(
    app: Application,
    private val getChannelByIdUseCase: GetChannelByIdUseCase,
    private val getChannelMessagesUseCase: GetChannelMessagesUseCase,
    private val sendChannelMessageUseCase: SendChannelMessageUseCase,
    private val clearUnreadCountUseCase: ClearUnreadCountUseCase,
    private val getCurrentUser: GetCurrentUser,
    private val getChannelMembersUseCase: GetChannelMembersUseCase,
) : BaseUiStateViewModel<ChannelChatUiState, ChannelChatUiEvent, ChannelChatUiAction>(app) {
    override fun initialState(): ChannelChatUiState = ChannelChatUiState()
    private var channelId: UUID? = null
    private val currentUserId = UUID.randomUUID()

    fun setChannelId(id: UUID) {
        channelId = id
        loadChannel()
        loadMessages()
        clearUnreadCount()
        loadCurrentUser()
        loadChannelMembers()
    }

    override fun handleAction(action: ChannelChatUiAction) {
        when (action) {
            is ChannelChatUiAction.SendMessage -> sendMessage(action.text)
            is ChannelChatUiAction.LoadMessages -> loadMessages()
            is ChannelChatUiAction.ClearUnread -> clearUnreadCount()
        }
    }

    private fun loadCurrentUser() {
        viewModelScope.launch(Dispatchers.IO) {
            val user = getCurrentUser()
            updateState { it.copy(currentUser = user) }
        }
    }

    private fun loadChannel() {
        val id = channelId ?: return
        viewModelScope.launch(Dispatchers.IO) {
            val channel = getChannelByIdUseCase(id)
            updateState { it.copy(channel = channel) }

        }
    }

    private fun loadMessages() {
        val id = channelId ?: return
        viewModelScope.launch(Dispatchers.IO) {
            try {
                updateState { it.copy(isLoading = true, error = null) }

                getChannelMessagesUseCase(id)
                    .catch { exception ->
                        handleError("Failed to load messages", exception)
                    }
                    .collectLatest { messages ->
                        updateState {
                            it.copy(
                                isLoading = false,
                                messages = messages,
                                error = null
                            )
                        }
                    }
            } catch (e: Exception) {
                handleError("Failed to load messages", e)
            }
        }
    }

    private fun sendMessage(text: String) {
        if (text.isBlank()) return
        val id = channelId ?: return
        val currentUser = uiState.value.currentUser ?: return
        viewModelScope.launch(Dispatchers.IO) {
            val message = ChannelMessage(
                channelId = id,
                senderId = currentUserId,
                senderName = currentUser.name,
                text = text,
                timestamp = System.currentTimeMillis()
            )
            sendChannelMessageUseCase(message)
            sendEvent(ChannelChatUiEvent.MessageSent)
        }
    }

    private fun clearUnreadCount() {
        val id = channelId ?: return
        viewModelScope.launch(Dispatchers.IO) {
            clearUnreadCountUseCase(id)
        }
    }

    private fun handleError(message: String, exception: Throwable) {
        val errorMsg = "$message: ${exception.message}"
        updateState { it.copy(isLoading = false, error = errorMsg) }
        sendEvent(ChannelChatUiEvent.Error(errorMsg))
        android.util.Log.e("ChannelChatViewModel", errorMsg, exception)
    }

    private fun loadChannelMembers() {
        val id = channelId ?: return
        viewModelScope.launch(Dispatchers.IO) {
            getChannelMembersUseCase(id)
                .catch { exception ->
                    android.util.Log.e("ChannelChatVM", "Error loading members", exception)
                }
                .collectLatest { members ->
                    updateState { it.copy(members = members) }
                }
        }
    }

}
