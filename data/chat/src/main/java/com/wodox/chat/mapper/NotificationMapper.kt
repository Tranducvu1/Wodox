package com.wodox.chat.mapper

import com.wodox.data.common.base.AbstractMapper
import com.wodox.chat.model.NotificationEntity
import com.wodox.domain.chat.model.local.Notification
import com.wodox.domain.chat.model.local.NotificationActionType
import java.util.UUID
import javax.inject.Inject

class NotificationMapper @Inject constructor() :
    AbstractMapper<NotificationEntity, Notification>() {

    override fun mapToDomain(entity: NotificationEntity): Notification {
        return Notification(
            id = UUID.fromString(entity.id), // ← String to UUID
            userId = UUID.fromString(entity.userId), // ← String to UUID
            fromUserId = UUID.fromString(entity.fromUserId), // ← String to UUID
            content = entity.content,
            createdAt = entity.createdAt,
            readAt = entity.readAt,
            dismissedAt = entity.dismissedAt,
            deletedAt = entity.deletedAt,
            fromUserName = entity.fromUserName,
            userAvatar = entity.userAvatar,
            taskId = UUID.fromString(entity.taskId), // ← String to UUID
            taskName = entity.taskName,
            actionType = try {
                NotificationActionType.valueOf(entity.actionType)
            } catch (e: Exception) {
                NotificationActionType.ASSIGNED
            },
            timestamp = entity.createdAt.time,
            isRead = entity.isRead,
            isDismissed = entity.isDismissed,
            updatedAt = entity.updatedAt ?: entity.createdAt
        )
    }

    override fun mapToEntity(domain: Notification): NotificationEntity {
        return NotificationEntity(
            id = domain.id.toString(),
            userId = domain.userId.toString(),
            fromUserId = domain.fromUserId.toString(),
            fromUserName = domain.fromUserName,
            userAvatar = domain.userAvatar,
            taskId = domain.taskId.toString(),
            taskName = domain.taskName,
            actionType = domain.actionType.name,
            content = domain.content,
            isRead = domain.isRead,
            isDismissed = domain.isDismissed,
            createdAt = domain.createdAt,
            readAt = domain.readAt,
            dismissedAt = domain.dismissedAt,
            updatedAt = domain.updatedAt,
            deletedAt = domain.deletedAt
        )
    }
}