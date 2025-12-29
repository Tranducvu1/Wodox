package com.wodox.chat.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.wodox.chat.model.local.NotificationActionType
import java.util.Date
import java.util.UUID

@Entity(
    tableName = "Notification",
    indices = [
        Index(value = ["id"], unique = true),
    ]
)

data class NotificationEntity(
    @PrimaryKey
    var id: UUID = UUID.randomUUID(),

    var userId: UUID,

    var fromUserId: UUID,

    var fromUserName: String,

    var userAvatar: String,

    var taskId: UUID,

    var taskName: String,

    var actionType: String = NotificationActionType.ASSIGNED.name,

    var content: String? = null,

    var isRead: Boolean = false,

    var isDismissed: Boolean = false,

    var createdAt: Date = Date(),

    var readAt: Date? = null,

    var dismissedAt: Date? = null,

    var updatedAt : Date? = null,

    var deletedAt: Date? = null
)