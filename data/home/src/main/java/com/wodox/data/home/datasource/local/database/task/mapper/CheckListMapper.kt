package com.wodox.data.home.datasource.local.database.task.mapper

import com.wodox.base.base.AbstractMapper
import com.wodox.data.home.datasource.local.database.task.entity.CheckListEntity
import com.wodox.domain.home.model.local.CheckList
import javax.inject.Inject

class CheckListMapper @Inject constructor() :
    AbstractMapper<CheckListEntity, CheckList>() {

    override fun mapToDomain(entity: CheckListEntity): CheckList {
        return CheckList(
            id = entity.id,
            taskId = entity.taskId,
            description = entity.description,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt,
            deletedAt = entity.deletedAt
        )
    }

    override fun mapToEntity(domain: CheckList): CheckListEntity {
        return CheckListEntity(
            id = domain.id,
            taskId = domain.taskId,
            description = domain.description,
            createdAt = domain.createdAt,
            updatedAt = domain.updatedAt,
            deletedAt = domain.deletedAt
        )
    }
}

