package com.wodox.domain.chat.model

import java.util.UUID

data class Channel(
    val id: UUID = UUID.randomUUID(),
    val name: String,
    val description: String? = null,
    val iconUrl: String? = null,
    val creatorId: UUID,
    val createdAt: Long = System.currentTimeMillis(),
    val memberCount: Int = 0,
    val isPrivate: Boolean = false,
    val lastMessageText: String? = null,
    val lastMessageTime: Long? = null,
    val unreadCount: Int = 0,
    val isJoined: Boolean = false
)
