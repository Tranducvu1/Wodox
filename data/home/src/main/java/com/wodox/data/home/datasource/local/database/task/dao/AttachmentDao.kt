package com.wodox.data.home.datasource.local.database.task.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.wodox.data.home.datasource.local.database.task.entity.AttachmentEntity
import com.wodox.data.home.datasource.local.database.task.entity.TaskEntity
import com.wodox.data.home.repository.AttachmentRepositoryImpl
import kotlinx.coroutines.flow.Flow
import java.util.UUID

@Dao
interface AttachmentDao {
    @Query(
        """
        SELECT * FROM Attachment
        WHERE taskId = :taskId
        AND deletedAt IS NULL
        ORDER BY createdAt ASC
        """
    )
    fun getAttachmentByTask(taskId: UUID): Flow<List<AttachmentEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(task: AttachmentEntity): Long

    @Update
    suspend fun update(task: AttachmentEntity): Int

    @Transaction
    suspend fun save(task: AttachmentEntity): Long {
        val id = insert(task)
        return if (id == -1L) {
            update(task).toLong()
        } else {
            id
        }
    }
}