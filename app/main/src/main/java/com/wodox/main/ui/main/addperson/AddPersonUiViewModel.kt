package com.wodox.main.ui.main.addperson

import android.app.Application
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.wodox.core.base.viewmodel.BaseUiStateViewModel
import com.wodox.domain.chat.model.local.Notification
import com.wodox.domain.chat.model.local.NotificationActionType
import com.wodox.domain.chat.usecase.SaveNotificationUseCase
import com.wodox.domain.home.model.local.FriendStatus
import com.wodox.domain.home.model.local.UserFriend
import com.wodox.domain.home.usecase.AddFriendUseCase
import com.wodox.domain.home.usecase.FindRelationUseCase
import com.wodox.domain.home.usecase.Params
import com.wodox.domain.user.usecase.GetCurrentUserEmail
import com.wodox.domain.user.usecase.GetUserByEmailUseCase
import com.wodox.domain.user.usecase.GetUserById
import com.wodox.domain.user.usecase.GetUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Date
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class AddPersonUiViewModel @Inject constructor(
    val app: Application,
    private val getUserByEmailUseCase: GetUserByEmailUseCase,
    private val getCurrentUserEmail: GetCurrentUserEmail,
    private val addFriendUseCase: AddFriendUseCase,
    private val getUserUseCase: GetUserUseCase,
    private val findRelationUseCase: FindRelationUseCase,
    private val saveNotificationUseCase: SaveNotificationUseCase,
    private val getUserById: GetUserById
) : BaseUiStateViewModel<AddPersonUiState, AddPersonUiEvent, AddPersonUiAction>(app) {

    override fun initialState(): AddPersonUiState = AddPersonUiState()

    override fun handleAction(action: AddPersonUiAction) {
        when (action) {
            is AddPersonUiAction.FindUserEmail ->
                handleGetUserByEmailUseCase(action.email)

            is AddPersonUiAction.HandleMakeFriend ->
                handleAddFriend(action.userId)
        }
    }

    init {
        loadUser()
    }

    private fun handleGetUserByEmailUseCase(email: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                updateState { it.copy(isLoading = true) }

                val currentEmail = uiState.value.email
                val currentUserId = uiState.value.userId

                if (email.isBlank()) {
                    updateState { it.copy(users = emptyList(), isLoading = false) }
                    return@launch
                }

                if (email.equals(currentEmail, ignoreCase = true)) {
                    updateState {
                        it.copy(
                            users = emptyList(),
                            isLoading = false,
                            errorMessage = "Cannot add yourself as friend"
                        )
                    }
                    return@launch
                }

                val user = getUserByEmailUseCase(email)

                val users = if (user != null && user.id != currentUserId) {
                    listOf(user)
                } else emptyList()

                updateState {
                    it.copy(
                        users = users,
                        isLoading = false,
                        errorMessage = if (users.isEmpty()) "User not found" else null
                    )
                }

            } catch (e: Exception) {
                Log.e("AddPerson", "Find user error", e)
                updateState {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message
                    )
                }
            }
        }
    }

    private fun loadUser() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val email = getCurrentUserEmail()
                val userId = getUserUseCase()

                updateState {
                    it.copy(
                        email = email,
                        userId = userId
                    )
                }
            } catch (e: Exception) {
                Log.e("AddPerson", "Load user error", e)
            }
        }
    }


    private fun handleAddFriend(connectId: UUID) {
        viewModelScope.launch(Dispatchers.IO) {
            android.util.Log.d("AddFriend", "========================================")
            android.util.Log.d("AddFriend", "HANDLE ADD FRIEND - START")
            android.util.Log.d("AddFriend", "========================================")

            val currentUserId = uiState.value.userId
            android.util.Log.d("AddFriend", "Current User ID: $currentUserId")
            android.util.Log.d("AddFriend", "Connect ID: $connectId")

            if (currentUserId == null) {
                android.util.Log.e("AddFriend", "❌ Current user ID is NULL - ABORTING")
                return@launch
            }

            if (currentUserId == connectId) {
                android.util.Log.w("AddFriend", "❌ Cannot add yourself")
                return@launch
            }

            android.util.Log.d("AddFriend", "Checking existing relation...")
            val existingRelation = findRelationUseCase(Params(currentUserId, connectId))

            if (existingRelation != null) {
                android.util.Log.w(
                    "AddFriend",
                    "❌ Relation already exists (id=${existingRelation.id}) - SKIP"
                )
                return@launch
            }

            android.util.Log.d("AddFriend", "Creating new friend relation...")
            val newRelation = UserFriend(
                id = UUID.randomUUID(),
                userId = currentUserId,
                friendId = connectId,
                status = FriendStatus.PENDING,
                createdAt = Date()
            )

            android.util.Log.d("AddFriend", "Saving friend relation...")
            addFriendUseCase(newRelation)
            android.util.Log.d("AddFriend", "✅ Friend relation saved")

            android.util.Log.d("AddFriend", "Creating friend request notification...")
            createFriendRequestNotification(
                fromUserId = currentUserId,
                toUserId = connectId
            )

            android.util.Log.d("AddFriend", "✅ Friend request completed successfully")
            android.util.Log.d("AddFriend", "========================================")

            sendEvent(AddPersonUiEvent.AddFriendSuccess)
            updateState { it.copy(users = emptyList()) }
        }
    }

    private suspend fun createFriendRequestNotification(
        fromUserId: UUID,
        toUserId: UUID
    ) {
        android.util.Log.d("AddFriend", "----------------------------------------")
        android.util.Log.d("AddFriend", "CREATE NOTIFICATION - START")
        android.util.Log.d("AddFriend", "  From User ID: $fromUserId")
        android.util.Log.d("AddFriend", "  To User ID: $toUserId")

        try {
            // Get sender info
            android.util.Log.d("AddFriend", "Fetching sender user info...")
            val fromUser = getUserById(fromUserId)
            val fromUserName = fromUser?.name ?: "Someone"
            val fromUserAvatar = fromUser?.avatar ?: ""

            android.util.Log.d("AddFriend", "  Sender name: $fromUserName")
            android.util.Log.d("AddFriend", "  Sender avatar: $fromUserAvatar")

            val notificationId = UUID.randomUUID()
            android.util.Log.d("AddFriend", "  Generated notification ID: $notificationId")

            val notification = Notification(
                id = notificationId,
                userId = toUserId,
                fromUserId = fromUserId,
                fromUserName = fromUserName,
                userAvatar = fromUserAvatar,
                taskId = UUID.randomUUID(),
                taskName = "",
                actionType = NotificationActionType.MAKE_FRIEND,
                content = "$fromUserName sent you a friend request",
                timestamp = System.currentTimeMillis(),
                createdAt = Date(),
                updatedAt = Date(),
                readAt = null,
                dismissedAt = null,
                deletedAt = null,
                isRead = false,
                isDismissed = false
            )

            android.util.Log.d("AddFriend", "Notification object created:")
            android.util.Log.d("AddFriend", "  ID: ${notification.id}")
            android.util.Log.d("AddFriend", "  UserId: ${notification.userId}")
            android.util.Log.d("AddFriend", "  FromUserId: ${notification.fromUserId}")
            android.util.Log.d("AddFriend", "  Content: ${notification.content}")
            android.util.Log.d("AddFriend", "  ActionType: ${notification.actionType}")
            android.util.Log.d("AddFriend", "  Timestamp: ${notification.timestamp}")

            android.util.Log.d("AddFriend", "Saving notification...")
            saveNotificationUseCase(notification)

            android.util.Log.d("AddFriend", "✅ NOTIFICATION CREATED SUCCESSFULLY!")
            android.util.Log.d("AddFriend", "----------------------------------------")

        } catch (e: Exception) {
            android.util.Log.e("AddFriend", "========================================")
            android.util.Log.e("AddFriend", "❌ ERROR CREATING NOTIFICATION")
            android.util.Log.e("AddFriend", "Error message: ${e.message}")
            android.util.Log.e("AddFriend", "Error cause: ${e.cause}")
            e.printStackTrace()
            android.util.Log.e("AddFriend", "========================================")
        }
    }

}
