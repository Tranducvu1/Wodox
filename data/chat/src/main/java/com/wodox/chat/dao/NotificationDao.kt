package com.wodox.chat.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.wodox.chat.model.NotificationEntity
import kotlinx.coroutines.flow.Flow
import java.util.UUID

@Dao
interface NotificationDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(notification: NotificationEntity): Long

    @Update
    suspend fun update(notification: NotificationEntity): Int

    @Transaction
    suspend fun save(notification: NotificationEntity): Long {
        val id = insert(notification)
        return if (id == -1L) {
            update(notification).toLong()
        } else {
            id
        }
    }

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertNotifications(notifications: List<NotificationEntity>)

    @Query(
        """
        SELECT * FROM Notification 
        WHERE userId = :userId 
        AND deletedAt IS NULL
        AND isDismissed = 0
        ORDER BY createdAt DESC
        """
    )
    fun getNotificationsByUser(userId: UUID): Flow<List<NotificationEntity>>

    @Query(
        """
        SELECT * FROM Notification 
        WHERE id = :id
        AND deletedAt IS NULL
        """
    )
    suspend fun getNotificationById(id: UUID): NotificationEntity?

    @Query(
        """
        SELECT * FROM Notification 
        WHERE taskId = :taskId
        AND isRead = 1 
        AND deletedAt IS NULL
        """
    )
    fun getNotificationMarkReadById(taskId: UUID): Flow<List<NotificationEntity>>

    @Query(
        """
        SELECT * FROM Notification 
        WHERE taskId = :taskId 
        AND deletedAt IS NULL
        ORDER BY createdAt DESC
        """
    )
    fun getNotificationsByTask(taskId: UUID): Flow<List<NotificationEntity>>
}