package com.wodox.domain.chat.repository

import com.wodox.domain.chat.model.local.Notification
import kotlinx.coroutines.flow.Flow
import java.util.UUID

interface NotificationRepository {

    suspend fun save(notification: Notification): Notification?

    suspend fun insertAll(notifications: List<Notification>)

    fun getNotificationByUserId(userId: UUID): Flow<List<Notification>>

    fun getNotificationByTaskId(taskId: UUID): Flow<List<Notification>>

    fun getByTask(taskId: UUID): Flow<List<Notification>>

    suspend fun markAsRead(notificationId: UUID)

    suspend fun markAllAsRead(userId: UUID)

    suspend fun update(notification: Notification)

    suspend fun updateAll(notifications: List<Notification>)

}
