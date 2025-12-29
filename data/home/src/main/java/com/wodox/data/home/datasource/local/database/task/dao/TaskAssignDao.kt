package com.wodox.data.home.datasource.local.database.task.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.wodox.data.home.datasource.local.database.task.entity.TaskAssigneeEntity
import java.util.UUID

@Dao
interface TaskAssignDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTaskAssignee(taskAssignee: TaskAssigneeEntity): Long

    @Query(
        """
        SELECT * FROM TaskAssignees
        WHERE taskId = :taskId
        AND deletedAt IS NULL
        """
    )
    suspend fun getTaskAssignByTaskId(taskId: UUID): TaskAssigneeEntity?

    @Query(
        """
        SELECT * FROM TaskAssignees
        WHERE UserId = :userId
        AND deletedAt IS NULL
        """
    )
    suspend fun getTaskAssignByUserId(userId: UUID): List<TaskAssigneeEntity>
}