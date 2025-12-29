package com.wodox.domain.chat.model.local

import com.wodox.chat.model.local.NotificationActionType
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

data class Notification(
    val id: UUID,
    val userId: UUID,
    val content: String?,
    var createdAt: Date = Date(),
    var readAt: Date? = null,
    var dismissedAt: Date? = null,
    var deletedAt: Date? = null,
    val fromUserId: UUID,
    val fromUserName: String,
    val userAvatar: String,
    val taskId: UUID,
    val updatedAt: Date = Date(),
    val taskName: String,
    val actionType: NotificationActionType = NotificationActionType.ASSIGNED,
    val timestamp: Long,
    val isRead: Boolean = false,
    val isDismissed: Boolean = false
) {
    val timeAgo: String
        get() = getTimeAgoString(timestamp)

    val notificationText: String
        get() = when (actionType) {
            NotificationActionType.ASSIGNED -> "$fromUserName assigned you a task"
            NotificationActionType.MENTIONED -> "$fromUserName mentioned you"
            NotificationActionType.COMPLETED -> "$fromUserName completed the task"
            NotificationActionType.COMMENTED -> "$fromUserName commented on task"
        }

    fun getTimeAgoString(timestamp: Long): String {
        val now = System.currentTimeMillis()

        val diff = now - timestamp

        return when {
            diff < 60000 -> "just now"

            diff < 3600000 -> "${diff / 60000} min ago"

            diff < 86400000 -> "${diff / 3600000} hour ago"

            diff < 604800000 -> "${diff / 86400000} day ago"

            else -> {
                val sdf = SimpleDateFormat("MMM dd", Locale.getDefault())
                sdf.format(Date(timestamp))
            }
        }
    }
}