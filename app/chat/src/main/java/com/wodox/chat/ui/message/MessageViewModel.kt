package com.wodox.chat.ui.message

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.wodox.chat.model.Constant
import com.wodox.core.base.viewmodel.BaseUiStateViewModel
import com.wodox.core.extension.serializable
import com.wodox.domain.chat.model.UserWithFriendStatus
import com.wodox.domain.chat.model.local.MessageChat
import com.wodox.domain.chat.usecase.*
import com.wodox.domain.user.usecase.GetUserById
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class MessageViewModel @Inject constructor(
    app: Application,
    private val getConversationMessagesUseCase: GetConversationMessagesUseCase,
    private val searchMessagesUseCase: SearchMessagesUseCase,
    private val sendMessageUseCase: SendMessageUseCase,
    private val updateMessageUseCase: UpdateMessageUseCase,
    private val deleteMessageUseCase: DeleteMessageUseCase,
    private val clearMessagesUseCase: ClearMessagesUseCase,
    private val getUserById: GetUserById,
) : BaseUiStateViewModel<MessageUiState, MessageUiEvent, MessageUiAction>(app) {

    override fun initialState(): MessageUiState = MessageUiState()
    private var user: UserWithFriendStatus? = null
    private var friendId: UUID? = null

    override fun handleAction(action: MessageUiAction) {
        when (action) {
            is MessageUiAction.SendMessage -> {
                sendMessage(action.text)
            }

            is MessageUiAction.UpdateMessage -> {
                updateMessage(action.messageId)
            }

            is MessageUiAction.DeleteMessage -> {
                deleteMessage(action.messageId)
            }

            is MessageUiAction.SearchMessages -> {
                searchMessages(action.query)
            }

            is MessageUiAction.ClearMessages -> {
                clearAllMessages()
            }

            MessageUiAction.LoadFriendUser -> {
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        user = data?.serializable<UserWithFriendStatus>(Constant.Intents.USER_ID)

        android.util.Log.d(
            "MESSAGE_DEBUG", """
            ===== UserWithFriendStatus =====
            currentUserId    = ${user?.currentUserId}
            relationUserId   = ${user?.relationUserId}
            relationFriendId = ${user?.relationFriendId}
            ===============================
        """.trimIndent()
        )

        if (user != null) {
            friendId = if (user?.currentUserId == user?.relationUserId) {
                user?.relationFriendId
            } else {
                user?.relationUserId
            }

            android.util.Log.d("MESSAGE_DEBUG", "Calculated friendId = $friendId")

            if (friendId != null) {
                loadFriendUser(friendId)
                getConversationMessages(user?.currentUserId, friendId)
            } else {
                updateState { it.copy(error = "Friend ID not found") }
            }
        } else {
            updateState { it.copy(error = "User data not found") }
        }
    }

    private fun loadFriendUser(userId: UUID?) {
        if (userId == null) return
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val friend = getUserById(userId)
                updateState {
                    it.copy(friend = friend)
                }
            } catch (e: Exception) {
                android.util.Log.e("MESSAGE_DEBUG", "Error loading friend: ${e.message}")
            }
        }
    }

    private fun sendMessage(text: String) {
        if (text.isBlank()) {
            updateState { it.copy(error = "Message cannot be empty") }
            return
        }

        val currentUserId = user?.currentUserId ?: return
        val receiverId = friendId ?: return

        viewModelScope.launch(Dispatchers.IO) {
            try {
                updateState { it.copy(isLoading = true, error = null) }

                val saveMessage = MessageChat(
                    text = text,
                    senderId = currentUserId,
                    receiverId = receiverId,
                    isCurrentUser = true
                )

                android.util.Log.d(
                    "MESSAGE_DEBUG", """
                    ===== Sending Message =====
                    senderId   = $currentUserId
                    receiverId = $receiverId
                    text       = $text
                    ===========================
                """.trimIndent()
                )

                val sentMessage = sendMessageUseCase(saveMessage)

                updateState {
                    it.copy(
                        isLoading = false,
                        error = null
                    )
                }
            } catch (e: Exception) {
                handleError("Failed to send message", e)
            }
        }
    }

    private fun getConversationMessages(currentUserId: UUID?, friendUserId: UUID?) {
        if (currentUserId == null || friendUserId == null) {
            updateState { it.copy(error = "Invalid conversation user") }
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            try {
                getConversationMessagesUseCase(currentUserId to friendUserId)
                    .collectLatest { messages ->
                        android.util.Log.d(
                            "MESSAGE_DEBUG", """
                            ===== Received Messages =====
                            currentUserId = $currentUserId
                            friendUserId  = $friendUserId
                            Total messages = ${messages.size}
                            User messages = ${messages.filter { it.isCurrentUser }.size}
                            Friend messages = ${messages.filterNot { it.isCurrentUser }.size}
                            =============================
                        """.trimIndent()
                        )

                        updateState {
                            it.copy(
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

    private fun searchMessages(query: String) {
        if (query.isBlank()) {
            updateState { it.copy(searchQuery = "", messages = emptyList()) }
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            try {
                updateState { it.copy(isLoading = true, searchQuery = query) }
                searchMessagesUseCase(query).collectLatest { results ->
                    updateState {
                        it.copy(
                            isLoading = false,
                            messages = results
                        )
                    }
                }
            } catch (e: Exception) {
                handleError("Failed to search messages", e)
            }
        }
    }

    private fun updateMessage(message: MessageChat) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                updateState { it.copy(isLoading = true) }

                updateMessageUseCase(message)

                updateState {
                    it.copy(
                        isLoading = false,
                        messages = it.messages.map { m ->
                            if (m.id == message.id) message else m
                        }
                    )
                }

                withContext(Dispatchers.Main) {
                    sendEvent(MessageUiEvent.MessageUpdated(message.id))
                }
            } catch (e: Exception) {
                handleError("Failed to update message", e)
            }
        }
    }

    private fun deleteMessage(messageId: UUID) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                updateState { it.copy(isLoading = true) }

                deleteMessageUseCase(messageId)

                updateState {
                    it.copy(
                        isLoading = false,
                        messages = it.messages.filterNot { m ->
                            m.id == messageId
                        }
                    )
                }

                withContext(Dispatchers.Main) {
                    sendEvent(MessageUiEvent.MessageDeleted(messageId))
                }
            } catch (e: Exception) {
                handleError("Failed to delete message", e)
            }
        }
    }

    private fun clearAllMessages() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                updateState { it.copy(isLoading = true) }
                clearMessagesUseCase()

                updateState {
                    it.copy(
                        isLoading = false,
                        messages = emptyList()
                    )
                }

                withContext(Dispatchers.Main) {
                    sendEvent(MessageUiEvent.AllMessagesCleared)
                }
            } catch (e: Exception) {
                handleError("Failed to clear messages", e)
            }
        }
    }

    private fun handleError(message: String, exception: Exception) {
        val errorMsg = "$message: ${exception.message}"
        updateState { it.copy(isLoading = false, error = errorMsg) }
        sendEvent(MessageUiEvent.Error(errorMsg))
        android.util.Log.e("MESSAGE_DEBUG", errorMsg, exception)
    }
}