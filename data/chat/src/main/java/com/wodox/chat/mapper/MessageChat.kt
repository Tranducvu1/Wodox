package com.wodox.chat.mapper

import com.wodox.chat.model.MessageChatEntity
import com.wodox.domain.chat.model.local.MessageChat
import com.wodox.domain.chat.model.local.MessageStatus
import java.util.UUID
import javax.inject.Inject

class MessageChatMapper @Inject constructor() {

    fun mapToEntity(domain: MessageChat): MessageChatEntity {
        return MessageChatEntity(
            id = domain.id.toString(),
            text = domain.text,
            senderId = domain.senderId?.toString() ?: "",
            receiverId = domain.receiverId?.toString() ?: "",
            timestamp = domain.timestamp,
            status = domain.status.name,
        )
    }

    fun mapToDomain(entity: MessageChatEntity): MessageChat {
        return MessageChat(
            id = try {
                UUID.fromString(entity.id)
            } catch (e: Exception) {
                UUID.randomUUID()
            },
            text = entity.text,
            senderId = if (entity.senderId.isNotEmpty()) {
                try {
                    UUID.fromString(entity.senderId)
                } catch (e: Exception) {
                    null
                }
            } else null,
            receiverId = if (entity.receiverId.isNotEmpty()) {
                try {
                    UUID.fromString(entity.receiverId)
                } catch (e: Exception) {
                    null
                }
            } else null,
            timestamp = entity.timestamp,
            status = try {
                MessageStatus.valueOf(entity.status)
            } catch (e: Exception) {
                MessageStatus.SENT
            },
            isCurrentUser = false
        )
    }
}