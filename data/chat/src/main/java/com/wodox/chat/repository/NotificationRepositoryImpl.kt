package com.wodox.chat.repository

import com.wodox.chat.dao.NotificationDao
import com.wodox.chat.mapper.NotificationMapper
import com.wodox.domain.chat.model.local.Notification
import com.wodox.domain.chat.repository.NotificationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Date
import java.util.UUID
import javax.inject.Inject

class NotificationRepositoryImpl @Inject constructor(
    private val dao: NotificationDao,
    private val mapper: NotificationMapper,
) : NotificationRepository {
    override suspend fun save(notification: Notification): Notification? {
        val entity = mapper.mapToEntity(notification).apply {
            this.updatedAt = Date()
        }
        dao.save(entity)
        return mapper.mapToDomain(entity)
    }

    override suspend fun insertAll(notifications: List<Notification>) {
        dao.insertNotifications(notifications.map { mapper.mapToEntity(it) })
    }

    override fun getNotificationByUserId(userId: UUID): Flow<List<Notification>> {
        return dao.getNotificationsByUser(userId)
            .map { list -> list.map { mapper.mapToDomain(it) } }
    }

    override fun getNotificationByTaskId(taskId: UUID): Flow<List<Notification>> {
        return dao.getNotificationMarkReadById(taskId)
            .map { list -> list.map { mapper.mapToDomain(it) } }
    }

    override fun getByTask(taskId: UUID): Flow<List<Notification>> {
        return dao.getNotificationsByTask(taskId)
            .map { list -> list.map { mapper.mapToDomain(it) } }
    }

    override suspend fun markAsRead(notificationId: UUID) {
        val entity = dao.getNotificationById(notificationId) ?: return
        dao.update(
            entity.copy(
                isRead = true,
                readAt = Date()
            )
        )
    }

    override suspend fun markAllAsRead(userId: UUID) {
        val notifications = dao.getNotificationsByUser(userId)
        notifications.collect { list ->
            val updatedList = list.map { it.copy(isRead = true, readAt = Date()) }
            updatedList.forEach { dao.update(it) }
        }
    }

    override suspend fun update(notification: Notification) {
        dao.update(mapper.mapToEntity(notification))
    }

    override suspend fun updateAll(notifications: List<Notification>) {
        notifications.forEach {
            dao.update(mapper.mapToEntity(it))
        }
    }
}