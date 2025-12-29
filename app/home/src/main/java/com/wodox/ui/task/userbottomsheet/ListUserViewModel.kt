package com.wodox.ui.task.userbottomsheet

import android.app.Application
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.wodox.domain.chat.model.UserWithFriendStatus
import com.wodox.core.base.viewmodel.BaseUiStateViewModel
import com.wodox.core.extension.toArrayList
import com.wodox.domain.home.model.local.UserFriend
import com.wodox.domain.home.usecase.GetFriendAcceptUseCase
import com.wodox.domain.home.usecase.GetFriendRequestUseCase
import com.wodox.domain.home.usecase.GetFriendSentUseCase
import com.wodox.domain.user.model.User
import com.wodox.domain.user.usecase.GetAllUserUseCase
import com.wodox.domain.user.usecase.GetUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class ListUserViewModel @Inject constructor(
    val app: Application,
    private val getFriendRequestsUseCase: GetFriendRequestUseCase,
    private val getFriendSentUseCase: GetFriendSentUseCase,
    private val getAcceptedFriendsUseCase: GetFriendAcceptUseCase,
    private val getAllUserUseCase: GetAllUserUseCase,
    private val getUserUseCase: GetUserUseCase,
) : BaseUiStateViewModel<ListUserUiState, ListUserUiEvent, ListUserUiAction>(app) {
    override fun initialState(): ListUserUiState = ListUserUiState()

    init {
        loadUser()
    }

    private fun loadUser() {
        viewModelScope.launch(Dispatchers.IO) {
            val currentUserId = getUserUseCase() ?: run {
                return@launch
            }
            combine(
                getFriendRequestsUseCase(currentUserId),
                getFriendSentUseCase(currentUserId),
                getAcceptedFriendsUseCase(currentUserId),
                getAllUserUseCase()
            ) { received, sent, accepted, allUsers ->
                val mapped = buildUserList(
                    currentUserId = currentUserId,
                    allUsers = allUsers,
                    received = received,
                    sent = sent,
                    accepted = accepted
                )
                mapped.forEach { Log.d(TAG, "  mapped -> $it") }
                mapped
            }.collect { list ->

                updateState { it.copy(listUser = list.toArrayList()) }
            }
        }
    }

    private fun buildUserList(
        currentUserId: UUID,
        allUsers: List<User>,
        received: List<UserFriend>,
        sent: List<UserFriend>,
        accepted: List<UserFriend>
    ): List<UserWithFriendStatus> {
        val mapList = { list: List<UserFriend> ->
            list.mapNotNull { relation ->
                val otherUserId =
                    if (relation.userId == currentUserId) relation.friendId
                    else relation.userId
                val user = allUsers.find { it.id == otherUserId }
                user?.let {
                    val mapped = UserWithFriendStatus(
                        it,
                        relation.status,
                        relationUserId = relation.userId,
                        relationFriendId = relation.friendId,
                        currentUserId = currentUserId
                    )
                    mapped
                }
            }
        }
        return mapList(received) + mapList(sent) + mapList(accepted)
    }

    companion object {
        private const val TAG = "ListUserViewModel"
    }

}
