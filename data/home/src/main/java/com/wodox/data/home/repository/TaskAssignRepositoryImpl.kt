package com.wodox.data.home.repository

import com.wodox.data.home.datasource.local.database.task.dao.TaskAssignDao
import com.wodox.data.home.datasource.local.database.task.mapper.TaskAssigneeMapper
import com.wodox.domain.home.model.local.TaskAssignee
import com.wodox.domain.home.repository.TaskAssignRepository
import java.util.UUID
import javax.inject.Inject

class TaskAssignRepositoryImpl @Inject constructor(
    private val taskAssignDao: TaskAssignDao,
    private val taskAssigneeMapper: TaskAssigneeMapper,
) : TaskAssignRepository {
    override suspend fun assignUserToTask(taskAssignee: TaskAssignee): TaskAssignee {
        val entity = taskAssigneeMapper.mapToEntity(taskAssignee)
        taskAssignDao.insertTaskAssignee(entity)
        return taskAssigneeMapper.mapToDomain(entity)
    }

    override suspend fun getTaskAssignByTaskID(id: UUID): TaskAssignee? {
        val entity = taskAssignDao.getTaskAssignByTaskId(id)
        return entity?.let { taskAssigneeMapper.mapToDomain(it) }
    }

    override suspend fun getTaskAssignByUserID(userId: UUID): List<TaskAssignee>? {
        val entity = taskAssignDao.getTaskAssignByUserId(userId)
        return entity.map { taskAssigneeMapper.mapToDomain(it) }
    }
}