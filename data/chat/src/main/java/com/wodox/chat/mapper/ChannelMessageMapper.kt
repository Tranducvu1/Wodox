package com.wodox.chat.mapper

import com.wodox.chat.model.ChannelMessageEntity
import com.wodox.data.common.base.AbstractMapper
import com.wodox.domain.chat.model.ChannelMessage
import com.wodox.domain.chat.model.MessageType
import java.util.UUID
import javax.inject.Inject

class ChannelMessageMapper @Inject constructor() :
    AbstractMapper<ChannelMessageEntity, ChannelMessage>() {
    override fun mapToDomain(entity: ChannelMessageEntity): ChannelMessage {
        return ChannelMessage(
            id = UUID.fromString(entity.id),
            channelId = UUID.fromString(entity.channelId),
            senderId = UUID.fromString(entity.senderId),
            senderName = entity.senderName,
            text = entity.text,
            timestamp = entity.timestamp,
            type = MessageType.valueOf(entity.type)
        )
    }

    override fun mapToEntity(domain: ChannelMessage): ChannelMessageEntity {
        return ChannelMessageEntity(
            id = domain.id.toString(),
            channelId = domain.channelId.toString(),
            senderId = domain.senderId.toString(),
            senderName = domain.senderName,
            text = domain.text,
            timestamp = domain.timestamp,
            type = domain.type.name
        )
    }
}