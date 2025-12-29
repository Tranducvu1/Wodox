package com.wodox.chat.mapper

import com.wodox.chat.model.MessageChatEntity
import com.wodox.data.common.base.AbstractMapper
import com.wodox.domain.chat.model.local.MessageChat
import javax.inject.Inject

class MessageChatMapper @Inject constructor() :
    AbstractMapper<MessageChatEntity, MessageChat>() {
    override fun mapToDomain(entity: MessageChatEntity): MessageChat {
        return MessageChat(
            id = entity.id,
            text = entity.text,
            senderId = entity.senderId,
            receiverId = entity.receiverId,
            timestamp = entity.timestamp,
            status = entity.status
        )
    }

    override fun mapToEntity(domain: MessageChat): MessageChatEntity {
        return MessageChatEntity(
            id = domain.id,
            text = domain.text,
            senderId = domain.senderId,
            receiverId = domain.receiverId,
            timestamp = domain.timestamp,
            status = domain.status
        )
    }
}
