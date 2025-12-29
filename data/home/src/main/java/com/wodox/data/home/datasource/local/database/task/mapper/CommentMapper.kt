package com.wodox.data.home.datasource.local.database.task.mapper

import com.wodox.base.base.AbstractMapper
import com.wodox.data.home.datasource.local.database.task.entity.CommentEntity
import com.wodox.domain.home.model.local.Comment
import javax.inject.Inject

class CommentMapper @Inject constructor() :
    AbstractMapper<CommentEntity, Comment>() {

    override fun mapToDomain(entity: CommentEntity): Comment {
        return Comment(
            id = entity.id,
            taskId = entity.taskId,
            content = entity.content,
            userId = entity.userId,
            userName = entity.userName,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt,
            deletedAt = entity.deletedAt
        )
    }

    override fun mapToEntity(domain: Comment): CommentEntity {
        return CommentEntity(
            id = domain.id,
            taskId = domain.taskId,
            content = domain.content,
            userId = domain.userId,
            userName = domain.userName,
            createdAt = domain.createdAt,
            updatedAt = domain.updatedAt,
            deletedAt = domain.deletedAt
        )
    }
}