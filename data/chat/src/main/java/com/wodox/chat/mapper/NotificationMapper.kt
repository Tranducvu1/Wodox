package com.wodox.chat.mapper

import com.wodox.data.common.base.AbstractMapper
import com.wodox.chat.model.NotificationEntity
import com.wodox.domain.chat.model.local.Notification
import com.wodox.chat.model.local.NotificationActionType
import javax.inject.Inject

class NotificationMapper @Inject constructor() :
    AbstractMapper<NotificationEntity, Notification>() {
    override fun mapToDomain(entity: NotificationEntity): Notification {
        return Notification(
            id = entity.id,
            userId = entity.userId,
            content = entity.content,
            createdAt = entity.createdAt,
            readAt = entity.readAt,
            dismissedAt = entity.dismissedAt,
            deletedAt = entity.deletedAt,
            fromUserId = entity.fromUserId,
            fromUserName = entity.fromUserName,
            userAvatar = entity.userAvatar,
            taskId = entity.taskId,
            taskName = entity.taskName,
            actionType = NotificationActionType.valueOf(entity.actionType),
            timestamp = entity.createdAt.time,
            isRead = entity.isRead,
            isDismissed = entity.isDismissed
        )
    }

    override fun mapToEntity(domain: Notification): NotificationEntity {
        return NotificationEntity(
            id = domain.id,
            userId = domain.userId,
            fromUserId = domain.fromUserId,
            fromUserName = domain.fromUserName,
            userAvatar = domain.userAvatar,
            taskId = domain.taskId,
            taskName = domain.taskName,
            actionType = domain.actionType.name,
            content = domain.content,
            isRead = domain.isRead,
            isDismissed = domain.isDismissed,
            createdAt = domain.createdAt,
            readAt = domain.readAt,
            dismissedAt = domain.dismissedAt,
            deletedAt = domain.deletedAt
        )
    }
}
