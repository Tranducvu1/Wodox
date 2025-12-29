package com.wodox.data.home.datasource.local.database.task.mapper

import com.wodox.data.home.datasource.local.database.task.entity.AiChatEntity
import com.wodox.domain.home.model.local.AiChat
import javax.inject.Inject

class AiChatMapper @Inject constructor() {

    fun mapToEntity(domain: AiChat): AiChatEntity {
        return AiChatEntity(
            id = domain.id,
            taskId = domain.taskId,
            userMessage = domain.userMessage,
            aiResponse = domain.aiResponse,
            timestamp = domain.timestamp,
            createdAt = domain.createdAt
        )
    }

    fun mapToDomain(entity: AiChatEntity): AiChat {
        return AiChat(
            id = entity.id,
            taskId = entity.taskId,
            userMessage = entity.userMessage,
            aiResponse = entity.aiResponse,
            timestamp = entity.timestamp,
            createdAt = entity.createdAt
        )
    }

    fun mapToDomainList(entities: List<AiChatEntity>): List<AiChat> {
        return entities.map { mapToDomain(it) }
    }
}