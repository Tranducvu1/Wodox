package com.wodox.data.home.datasource.local.database.task.mapper

import com.wodox.base.base.AbstractMapper
import com.wodox.data.home.datasource.local.database.task.entity.AttachmentEntity
import com.wodox.domain.home.model.local.Attachment
import com.wodox.domain.home.model.local.AttachmentType
import javax.inject.Inject

class AttachmentMapper @Inject constructor() :
    AbstractMapper<AttachmentEntity, Attachment>() {
    override fun mapToDomain(entity: AttachmentEntity): Attachment {
        return Attachment(
            id = entity.id,
            taskId = entity.taskId,
            name = entity.name,
            uri = entity.uri,
            type = entity.type?.let { AttachmentType.valueOf(it) },
            url = entity.url,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt,
            deletedAt = entity.deletedAt
        )
    }

    override fun mapToEntity(domain: Attachment): AttachmentEntity {
        return AttachmentEntity(
            id = domain.id,
            taskId = domain.taskId,
            name = domain.name,
            uri = domain.uri,
            type = domain.type?.name,
            url = domain.url,
            createdAt = domain.createdAt,
            updatedAt = domain.updatedAt,
            deletedAt = domain.deletedAt
        )
    }
}
