package com.wodox.data.home.datasource.local.database.task.dao
import androidx.paging.PagingSource
import androidx.room.*
import com.wodox.data.home.datasource.local.database.task.entity.SubTaskEntity
import kotlinx.coroutines.flow.Flow
import java.util.*

@Dao
interface SubTaskDao {
    @Query(
        """
        SELECT * FROM SubTask
        WHERE deletedAt IS NULL
        ORDER BY createdAt ASC
        """
    )
    fun getAllSubTasks(): Flow<List<SubTaskEntity>>

    @Query(
        """
        SELECT * FROM SubTask
        WHERE taskId = :taskId
        AND deletedAt IS NULL
        ORDER BY priority DESC, createdAt ASC
        """
    )
    fun getSubTasksByTaskId(taskId: UUID): Flow<List<SubTaskEntity>>

    @Query(
        """
        SELECT * FROM SubTask
        WHERE id = :subTaskId
        AND deletedAt IS NULL
        LIMIT 1
        """
    )
    suspend fun getSubTaskById(subTaskId: UUID): SubTaskEntity?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(subTask: SubTaskEntity): Long

    @Update
    suspend fun update(subTask: SubTaskEntity): Int

    @Transaction
    suspend fun save(subTask: SubTaskEntity): Long {
        val id = insert(subTask)
        return if (id == -1L) {
            update(subTask).toLong()
        } else {
            id
        }
    }

    @Query(
        """
        UPDATE SubTask
        SET deletedAt = :deletedAt
        WHERE id = :subTaskId
        """
    )
    suspend fun deleteSubTask(subTaskId: UUID, deletedAt: Date = Date()): Int

    @Transaction
    @Query("SELECT * FROM SubTask WHERE deletedAt IS NULL ORDER BY startAt DESC")
    fun getSubTaskPaging(): PagingSource<Int, SubTaskEntity>

    @Transaction
    @Query(
        """
        SELECT * FROM SubTask
        WHERE deletedAt IS NULL AND taskId = :taskId
        ORDER BY startAt DESC
        """
    )
    fun getSubTaskPagingByTask(taskId: UUID): PagingSource<Int, SubTaskEntity>
}
