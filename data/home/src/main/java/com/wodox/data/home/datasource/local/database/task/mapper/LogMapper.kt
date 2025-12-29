package com.wodox.data.home.datasource.local.database.task.mapper

import com.wodox.base.base.AbstractMapper
import com.wodox.data.home.datasource.local.database.task.entity.LogEntity
import com.wodox.domain.home.model.local.Log
import javax.inject.Inject

class LogMapper @Inject constructor() :
    AbstractMapper<LogEntity, Log>() {

    override fun mapToDomain(entity: LogEntity): Log {
        return Log(
            id = entity.id,
            taskId = entity.taskId,
            title = entity.title,
            description = entity.description,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt,
            deletedAt = entity.deletedAt
        )
    }

    override fun mapToEntity(domain: Log): LogEntity {
        return LogEntity(
            id = domain.id,
            taskId = domain.taskId,
            title = domain.title,
            description = domain.description,
            createdAt = domain.createdAt,
            updatedAt = domain.updatedAt,
            deletedAt = domain.deletedAt
        )
    }
}
