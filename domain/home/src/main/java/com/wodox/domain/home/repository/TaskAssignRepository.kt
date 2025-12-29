package com.wodox.domain.home.repository

import com.wodox.domain.home.model.local.TaskAssignee
import java.util.UUID

interface TaskAssignRepository {
    suspend fun assignUserToTask(taskAssignee: TaskAssignee): TaskAssignee
    suspend fun getTaskAssignByTaskID(id: UUID): TaskAssignee?
    suspend fun getTaskAssignByUserID(userId: UUID): List<TaskAssignee>?
}