package com.wodox.data.home.datasource.local.database.task.dao


import androidx.paging.PagingSource
import androidx.room.*
import com.wodox.data.home.datasource.local.database.task.entity.LogEntity
import kotlinx.coroutines.flow.Flow
import java.util.UUID

@Dao
interface LogDao {

    @Query(
        """
        SELECT * FROM Log
        WHERE deletedAt IS NULL
        ORDER BY createdAt DESC
    """
    )
    fun getAllLogs(): Flow<List<LogEntity>>

    @Query(
        """
        SELECT * FROM Log
        WHERE taskId = :taskId
        AND deletedAt IS NULL
    """
    )
    fun getLogByTaskId(taskId: UUID): Flow<List<LogEntity>>

    @Query(
        """
        SELECT * FROM Log
        WHERE deletedAt IS NULL
        ORDER BY createdAt DESC
    """
    )
    fun getLogPaging(): PagingSource<Int, LogEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(log: LogEntity): Long

    @Update
    suspend fun update(log: LogEntity): Int

    @Transaction
    suspend fun save(log: LogEntity): Long {
        val id = insert(log)
        return if (id == -1L) {
            update(log).toLong()
        } else {
            id
        }
    }

    @Query(
        """
        UPDATE Log
        SET deletedAt = CURRENT_TIMESTAMP
        WHERE id = :logId
    """
    )
    suspend fun softDelete(logId: UUID)
}
