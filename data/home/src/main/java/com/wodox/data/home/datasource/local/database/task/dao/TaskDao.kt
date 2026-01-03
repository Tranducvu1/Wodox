package com.wodox.data.home.datasource.local.database.task.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.wodox.data.home.datasource.local.database.task.entity.TaskEntity
import kotlinx.coroutines.flow.Flow
import java.util.UUID

@Dao
interface TaskDao {

    @Query(
        """
        SELECT * FROM Task
        WHERE deletedAt IS NULL
        ORDER BY createdAt ASC
        """
    )
    fun getAllTasks(): Flow<List<TaskEntity>>

    @Query(
        """
        SELECT * FROM Task
        WHERE projectId = :projectId
        AND deletedAt IS NULL
        ORDER BY priority DESC, createdAt ASC
        """
    )
    fun getTasksByProject(projectId: UUID): Flow<List<TaskEntity>>

    @Query(
        """
        SELECT * FROM Task
        WHERE id = :taskId
        AND deletedAt IS NULL
        LIMIT 1
        """
    )
    suspend fun getTaskById(taskId: UUID): TaskEntity?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(task: TaskEntity): Long

    @Update
    suspend fun update(task: TaskEntity): Int

    @Transaction
    suspend fun save(task: TaskEntity): Long {
        val id = insert(task)
        return if (id == -1L) {
            update(task).toLong()
        } else {
            id
        }
    }

    @Query(
        "SELECT Task.* FROM Task INNER JOIN TaskAssignees ON Task.id = TaskAssignees.taskId WHERE TaskAssignees.userId = :userId AND Task.deletedAt IS NULL ORDER BY Task.priority DESC, Task.createdAt ASC"
    )
    fun getTasksByUserId(userId: UUID): PagingSource<Int, TaskEntity>

    // ✅ NEW: Sắp xếp theo calculatedPriority
    @Transaction
    @Query(
        """
        SELECT DISTINCT Task.* FROM Task
        LEFT JOIN TaskAssignees ON Task.id = TaskAssignees.taskId
        WHERE Task.deletedAt IS NULL 
        AND (Task.ownerId = :userId OR TaskAssignees.userId = :userId)
        ORDER BY Task.calculatedPriority DESC
        """
    )
    fun getTasksForUserByPriority(userId: UUID): PagingSource<Int, TaskEntity>

    @Transaction
    @Query(
        """
        SELECT DISTINCT Task.* FROM Task
        LEFT JOIN TaskAssignees ON Task.id = TaskAssignees.taskId
        WHERE Task.deletedAt IS NULL 
        AND (Task.ownerId = :userId OR TaskAssignees.userId = :userId)
        ORDER BY 
            (Task.priority * 0.4 + Task.difficulty * 0.3 + Task.support * 0.2) DESC,
            CASE 
                WHEN Task.dueAt IS NULL THEN 0
                WHEN (julianday(Task.dueAt) - julianday('now')) < 0 THEN 10
                WHEN (julianday(Task.dueAt) - julianday('now')) <= 1 THEN 8
                WHEN (julianday(Task.dueAt) - julianday('now')) <= 3 THEN 7
                WHEN (julianday(Task.dueAt) - julianday('now')) <= 7 THEN 5
                ELSE 2
            END DESC,
            Task.createdAt DESC
        """
    )
    fun getTaskPaging(userId: UUID): PagingSource<Int, TaskEntity>

    // ✅ UPDATE: Sắp xếp theo calculatedPriority
    @Query(
        """
        SELECT * FROM Task 
        WHERE ownerId = :ownerId 
        AND deletedAt IS NULL 
        AND isFavourite = 1 
        ORDER BY Task.calculatedPriority DESC
        """
    )
    fun getTaskFavouritePagingByPriority(ownerId: UUID): PagingSource<Int, TaskEntity>

    @Transaction
    @Query(
        """
        SELECT * FROM Task 
        WHERE ownerId = :ownerId 
        AND deletedAt IS NULL 
        AND isFavourite = 1 
        ORDER BY 
            (priority * 0.4 + difficulty * 0.3 + support * 0.2) DESC,
            CASE 
                WHEN dueAt IS NULL THEN 0
                WHEN (julianday(dueAt) - julianday('now')) < 0 THEN 10
                WHEN (julianday(dueAt) - julianday('now')) <= 1 THEN 8
                WHEN (julianday(dueAt) - julianday('now')) <= 3 THEN 7
                WHEN (julianday(dueAt) - julianday('now')) <= 7 THEN 5
                ELSE 2
            END DESC,
            startAt DESC
        """
    )
    fun getTaskFavouritePaging(ownerId: UUID): PagingSource<Int, TaskEntity>

    @Query(
        """
        SELECT * FROM Task 
        WHERE ownerId = :userId 
        AND deletedAt IS NULL
        ORDER BY createdAt DESC
        """
    )
    suspend fun getAllTasksByUserId(userId: UUID): List<TaskEntity>

    @Query(
        """
        SELECT * FROM Task 
        WHERE ownerId = :userId 
        AND deletedAt IS NULL
        ORDER BY createdAt DESC
        """
    )
    fun getTasksNotificationByUserId(userId: UUID): Flow<List<TaskEntity>>

    @Transaction
    @Query(
        """
        SELECT DISTINCT Task.* FROM Task
        LEFT JOIN TaskAssignees ON Task.id = TaskAssignees.taskId
        WHERE Task.deletedAt IS NULL 
        AND (Task.ownerId = :userId OR TaskAssignees.userId = :userId)
        ORDER BY 
            CASE 
                WHEN Task.dueAt IS NULL THEN 999
                ELSE CAST((julianday(Task.dueAt) - julianday('now')) AS INTEGER)
            END ASC,
            Task.priority DESC,
            Task.difficulty DESC,
            Task.createdAt DESC
        """
    )
    fun getTasksForUser(userId: UUID): PagingSource<Int, TaskEntity>

    @Query("UPDATE Task SET calculatedPriority = :priority WHERE id = :taskId")
    suspend fun updateCalculatedPriority(taskId: UUID, priority: Double)

}
