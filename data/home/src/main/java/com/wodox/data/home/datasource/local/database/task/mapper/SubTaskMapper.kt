package com.wodox.data.home.datasource.local.database.task.mapper

import com.wodox.base.base.AbstractMapper
import com.wodox.data.home.datasource.local.database.task.entity.SubTaskEntity
import com.wodox.domain.home.model.local.SubTask
import javax.inject.Inject

class SubTaskMapper @Inject constructor() :
    AbstractMapper<SubTaskEntity, SubTask>() {

    override fun mapToDomain(entity: SubTaskEntity): SubTask {
        return SubTask(
            id = entity.id,
            taskId = entity.taskId,
            title = entity.title,
            description = entity.description,
            status = entity.status,
            priority = entity.priority,
            startAt = entity.startAt,
            dueAt = entity.dueAt,
            estimateAt = entity.estimateAt,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt,
            deletedAt = entity.deletedAt
        )
    }

    override fun mapToEntity(domain: SubTask): SubTaskEntity {
        return SubTaskEntity(
            id = domain.id,
            taskId = domain.taskId,
            title = domain.title,
            description = domain.description,
            status = domain.status,
            priority = domain.priority,
            startAt = domain.startAt,
            dueAt = domain.dueAt,
            estimateAt = domain.estimateAt,
            createdAt = domain.createdAt,
            updatedAt = domain.updatedAt,
            deletedAt = domain.deletedAt
        )
    }
}
