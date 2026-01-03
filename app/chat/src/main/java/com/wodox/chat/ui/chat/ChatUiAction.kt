package com.wodox.chat.ui.chat

import com.wodox.domain.chat.model.local.Notification
import java.util.UUID

sealed class ChatUiAction {
    data class AcceptFriend(val friendId: UUID) : ChatUiAction()
    data class RejectFriend(val friendId: UUID) : ChatUiAction()
    object LoadUser : ChatUiAction()
    data class MarkNotificationAsRead(val notificationId: Notification) : ChatUiAction()
    data class DismissNotification(val notificationId: Notification) : ChatUiAction()
    data class LoadTask(val taskId: UUID) : ChatUiAction()
}