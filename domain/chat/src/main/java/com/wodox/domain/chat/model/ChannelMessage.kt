package com.wodox.domain.chat.model

import java.util.UUID


data class ChannelMessage(
    val id: UUID = UUID.randomUUID(),
    val channelId: UUID,
    val senderId: UUID,
    val senderName: String,
    val text: String,
    val timestamp: Long = System.currentTimeMillis(),
    val type: MessageType = MessageType.TEXT
)

enum class MessageType {
    TEXT,
    IMAGE,
    FILE,
    SYSTEM
}