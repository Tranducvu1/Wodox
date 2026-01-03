package com.wodox.chat.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.google.firebase.firestore.PropertyName
import com.wodox.domain.chat.model.local.NotificationActionType
import java.util.Date

@Entity(
    tableName = "Notification",
    indices = [
        Index(value = ["id"], unique = true),
    ]
)
data class NotificationEntity(
    @PrimaryKey
    @PropertyName("id")
    var id: String = "",

    @PropertyName("userId")
    var userId: String = "",

    @PropertyName("fromUserId")
    var fromUserId: String = "",

    @PropertyName("fromUserName")
    var fromUserName: String = "",

    @PropertyName("userAvatar")
    var userAvatar: String = "",

    @PropertyName("taskId")
    var taskId: String = "",

    @PropertyName("taskName")
    var taskName: String = "",

    @PropertyName("actionType")
    var actionType: String = NotificationActionType.ASSIGNED.name,

    @PropertyName("content")
    var content: String? = null,

    @PropertyName("isRead")
    var isRead: Boolean = false,

    @PropertyName("isDismissed")
    var isDismissed: Boolean = false,

    @PropertyName("createdAt")
    var createdAt: Date = Date(),

    @PropertyName("readAt")
    var readAt: Date? = null,

    @PropertyName("dismissedAt")
    var dismissedAt: Date? = null,

    @PropertyName("updatedAt")
    var updatedAt: Date? = null,

    @PropertyName("deletedAt")
    var deletedAt: Date? = null
) {
    constructor() : this(
        "", "", "", "", "", "", "", "", null, false, false,
        Date(), null, null, null, null
    )
}