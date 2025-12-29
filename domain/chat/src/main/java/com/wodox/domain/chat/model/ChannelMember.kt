package com.wodox.domain.chat.model

import java.util.UUID

data class ChannelMember(
    val id: UUID = UUID.randomUUID(),
    val channelId: UUID,
    val userId: UUID,
    val role: ChannelRole = ChannelRole.MEMBER,
    val joinedAt: Long = System.currentTimeMillis()
)