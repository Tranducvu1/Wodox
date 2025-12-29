package com.wodox.chat.mapper

import com.wodox.chat.model.ChannelEntity
import com.wodox.data.common.base.AbstractMapper
import com.wodox.domain.chat.model.Channel

import java.util.UUID
import javax.inject.Inject

class ChannelMapper @Inject constructor() :
    AbstractMapper<ChannelEntity, Channel>() {
    override fun mapToDomain(entity: ChannelEntity): Channel {
        return Channel(
            id = UUID.fromString(entity.id),
            name = entity.name,
            description = entity.description,
            iconUrl = entity.iconUrl,
            creatorId = UUID.fromString(entity.creatorId),
            createdAt = entity.createdAt,
            memberCount = entity.memberCount,
            isPrivate = entity.isPrivate,
            lastMessageText = entity.lastMessageText,
            lastMessageTime = entity.lastMessageTime,
            unreadCount = entity.unreadCount,
            isJoined = entity.isJoined
        )
    }

    override fun mapToEntity(domain: Channel): ChannelEntity {
        return ChannelEntity(
            id = domain.id.toString(),
            name = domain.name,
            description = domain.description,
            iconUrl = domain.iconUrl,
            creatorId = domain.creatorId.toString(),
            createdAt = domain.createdAt,
            memberCount = domain.memberCount,
            isPrivate = domain.isPrivate,
            lastMessageText = domain.lastMessageText,
            lastMessageTime = domain.lastMessageTime,
            unreadCount = domain.unreadCount,
            isJoined = domain.isJoined
        )
    }
}