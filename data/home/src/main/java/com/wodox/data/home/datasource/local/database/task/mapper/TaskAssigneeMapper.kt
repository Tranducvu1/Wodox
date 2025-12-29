package com.wodox.data.home.datasource.local.database.task.mapper


import com.wodox.base.base.AbstractMapper
import com.wodox.data.home.datasource.local.database.task.entity.TaskAssigneeEntity
import com.wodox.domain.home.model.local.TaskAssignee
import javax.inject.Inject

class TaskAssigneeMapper @Inject constructor() :
    AbstractMapper<TaskAssigneeEntity, TaskAssignee>() {
    override fun mapToDomain(entity: TaskAssigneeEntity): TaskAssignee {
        return TaskAssignee(
            id = entity.id,
            taskId = entity.taskId,
            userId = entity.userId,
            ownerId = entity.ownerId,
            assignedAt = entity.assignedAt,
            deletedAt = entity.deletedAt
        )
    }

    override fun mapToEntity(domain: TaskAssignee): TaskAssigneeEntity {
        return TaskAssigneeEntity(
            id = domain.id,
            taskId = domain.taskId,
            userId = domain.userId,
            ownerId = domain.ownerId,
            assignedAt = domain.assignedAt,
            deletedAt = domain.deletedAt
        )
    }
}