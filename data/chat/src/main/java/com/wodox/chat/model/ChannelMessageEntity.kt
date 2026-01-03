package com.wodox.chat.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID


@Entity(tableName = "channel_messages")
data class ChannelMessageEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val channelId: String = "",
    val senderId: String = "",
    val senderName: String = "",
    val text: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val type: String = "TEXT"
)