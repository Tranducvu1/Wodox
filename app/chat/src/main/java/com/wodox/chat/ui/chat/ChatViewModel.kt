package com.wodox.chat.ui.chat

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.wodox.domain.chat.model.UserWithFriendStatus
import com.wodox.domain.chat.model.local.Notification
import com.wodox.domain.chat.usecase.GetNotificationByUserIdUseCase
import com.wodox.domain.chat.usecase.MarkAsReadNotificationUseCase
import com.wodox.domain.chat.usecase.SaveNotificationUseCase
import com.wodox.core.base.viewmodel.BaseUiStateViewModel
import com.wodox.core.extension.toArrayList
import com.wodox.domain.chat.model.Channel
import com.wodox.domain.home.model.local.FriendStatus
import com.wodox.domain.home.model.local.UserFriend
import com.wodox.domain.home.usecase.AddFriendUseCase
import com.wodox.domain.home.usecase.FindRelationUseCase
import com.wodox.domain.home.usecase.GetFriendAcceptUseCase
import com.wodox.domain.home.usecase.GetFriendRequestUseCase
import com.wodox.domain.home.usecase.GetFriendSentUseCase
import com.wodox.domain.home.usecase.GetTaskByTaskIdUseCase
import com.wodox.domain.home.usecase.Params
import com.wodox.domain.user.model.User
import com.wodox.domain.user.usecase.GetAllUserUseCase
import com.wodox.domain.user.usecase.GetUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Date
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    val app: Application,
    private val getFriendRequestsUseCase: GetFriendRequestUseCase,
    private val getFriendSentUseCase: GetFriendSentUseCase,
    private val getAcceptedFriendsUseCase: GetFriendAcceptUseCase,
    private val getAllUserUseCase: GetAllUserUseCase,
    private val getUserUseCase: GetUserUseCase,
    private val addFriendUseCase: AddFriendUseCase,
    private val findRelationUseCase: FindRelationUseCase,
    private val getNotificationByUserIdUseCase: GetNotificationByUserIdUseCase,
    private val markAsReadNotificationUseCase: MarkAsReadNotificationUseCase,
    private val saveNotificationUseCase: SaveNotificationUseCase,
    private val getTaskByTaskIdUseCase: GetTaskByTaskIdUseCase,
) : BaseUiStateViewModel<ChatUiState, ChatUiEvent, ChatUiAction>(app) {
    private var previousNotificationCount = 0
    
    override fun initialState(): ChatUiState = ChatUiState()

    override fun onCreate() {
        super.onCreate()
        loadUser()
        loadNotifications()
        loadChannels()
    }

    override fun handleAction(action: ChatUiAction) {
        when (action) {
            is ChatUiAction.AcceptFriend -> updateFriendStatus(
                action.friendId,
                FriendStatus.ACCEPTED
            )
            is ChatUiAction.RejectFriend -> updateFriendStatus(
                action.friendId,
                FriendStatus.REJECTED
            )
            is ChatUiAction.LoadUser -> loadUser()
            is ChatUiAction.MarkNotificationAsRead -> markNotificationAsRead(action.notificationId)
            is ChatUiAction.DismissNotification -> dismissNotification(action.notificationId)
            is ChatUiAction.LoadTask -> loadTask(action.taskId)
        }
    }

    private fun loadNotifications() {
        viewModelScope.launch(Dispatchers.IO) {
            val currentUserId = getUserUseCase() ?: return@launch
            getNotificationByUserIdUseCase(currentUserId).collect { notifications ->
                val unreadCount = notifications.count { !it.isRead }

                val shouldAnimate = unreadCount > previousNotificationCount

                updateState {
                    it.copy(
                        listNotifications = notifications
                            .sortedByDescending { it.createdAt }
                            .toArrayList(),
                        unreadNotificationCount = unreadCount,
                        hasNewNotification = shouldAnimate
                    )
                }

                if (shouldAnimate) {
                    withContext(Dispatchers.Main) {
                        sendEvent(ChatUiEvent.ResetNotificationAnimation)
                    }
                }
                previousNotificationCount = unreadCount
            }
        }
    }

    private fun loadUser() {
        viewModelScope.launch(Dispatchers.IO) {
            val currentUserId = getUserUseCase() ?: return@launch
            combine(
                getFriendRequestsUseCase(currentUserId),
                getFriendSentUseCase(currentUserId),
                getAcceptedFriendsUseCase(currentUserId),
                getAllUserUseCase()
            ) { received, sent, accepted, allUsers ->
                val allMapped = buildUserList(
                    currentUserId = currentUserId,
                    allUsers = allUsers,
                    received = received,
                    sent = sent,
                    accepted = accepted
                )
                allMapped
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
                    UserWithFriendStatus(
                        it, relation.status,
                        relationUserId = relation.userId,
                        relationFriendId = relation.friendId,
                        currentUserId = currentUserId
                    )
                }
            }
        }
        return mapList(received) + mapList(sent) + mapList(accepted)
    }

    private fun updateFriendStatus(friendId: UUID, newStatus: FriendStatus) {
        viewModelScope.launch(Dispatchers.IO) {
            val currentUserId = getUserUseCase() ?: return@launch
            val relation = findRelationUseCase(Params(currentUserId, friendId)) ?: return@launch
            if (!isActionAllowed(currentUserId, relation, newStatus)) {
                return@launch
            }
            addFriendUseCase(relation.copy(status = newStatus))
        }
    }

    private fun markNotificationAsRead(notificationId: UUID) {
        viewModelScope.launch(Dispatchers.IO) {
            markAsReadNotificationUseCase(notificationId)
        }
    }

    private fun dismissNotification(notification: Notification) {
        viewModelScope.launch(Dispatchers.IO) {
            val updatedNotification = notification.copy(
                deletedAt = Date()
            )
            saveNotificationUseCase(updatedNotification)
        }
    }

    private fun isActionAllowed(
        currentUserId: UUID,
        relation: UserFriend,
        newStatus: FriendStatus
    ): Boolean {
        val isSender = relation.userId == currentUserId
        val isReceiver = relation.friendId == currentUserId
        return when (newStatus) {
            FriendStatus.ACCEPTED -> isReceiver
            FriendStatus.REJECTED -> isSender
            else -> false
        }
    }

    private fun loadTask(taskId: UUID) {
        viewModelScope.launch(Dispatchers.IO) {
            val task = getTaskByTaskIdUseCase(taskId)
            updateState { it.copy(task = task) }
            sendEvent(ChatUiEvent.NavigatorTask)
        }
    }

    private fun loadChannels() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val mockChannels = listOf(
                    Channel(
                        id = UUID.randomUUID(),
                        name = "General",
                        creatorId = UUID.randomUUID(),
                        memberCount = 125,
                        unreadCount = 3
                    ),
                    Channel(
                        id = UUID.randomUUID(),
                        name = "Random",
                        creatorId = UUID.randomUUID(),
                        memberCount = 85,
                        unreadCount = 0
                    ),
                    Channel(
                        id = UUID.randomUUID(),
                        name = "Development",
                        creatorId = UUID.randomUUID(),
                        memberCount = 42,
                        unreadCount = 7,
                        isPrivate = true
                    )
                )

                updateState { it.copy(channels = mockChannels) }
            } catch (e: Exception) {
                android.util.Log.e("ChatViewModel", "Error loading channels: ${e.message}")
            }
        }
    }
}