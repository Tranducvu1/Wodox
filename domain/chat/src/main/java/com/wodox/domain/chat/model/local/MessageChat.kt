package com.wodox.domain.chat.model.local

import java.util.UUID

data class MessageChat(
    val id: UUID = UUID.randomUUID(),
    var text: String,
    val isCurrentUser: Boolean = false,
    val senderId: UUID? = null,
    val receiverId: UUID? = null,
    val timestamp: Long = System.currentTimeMillis(),
    val status: MessageStatus = MessageStatus.SENT
)
