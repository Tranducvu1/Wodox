package com.wodox.chat.mapper

import com.wodox.chat.model.ChannelMemberEntity
import com.wodox.data.common.base.AbstractMapper
import com.wodox.domain.chat.model.ChannelMember
import com.wodox.domain.chat.model.ChannelRole
import java.util.UUID
import javax.inject.Inject

class ChannelMemberMapper @Inject constructor() :
    AbstractMapper<ChannelMemberEntity, ChannelMember>() {

    override fun mapToDomain(entity: ChannelMemberEntity): ChannelMember {
        return ChannelMember(
            id = UUID.fromString(entity.id),
            channelId = UUID.fromString(entity.channelId),
            userId = UUID.fromString(entity.userId),
            role = ChannelRole.valueOf(entity.role),
            joinedAt = entity.joinedAt
        )
    }

    override fun mapToEntity(domain: ChannelMember): ChannelMemberEntity {
        return ChannelMemberEntity(
            id = domain.id.toString(),
            channelId = domain.channelId.toString(),
            userId = domain.userId.toString(),
            role = domain.role.name,
            joinedAt = domain.joinedAt
        )
    }
}