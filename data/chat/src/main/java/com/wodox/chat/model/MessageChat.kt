package com.wodox.chat.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.wodox.domain.chat.model.local.MessageStatus
import java.util.UUID


@Entity(
    tableName = "messages",
    indices = [
        Index(value = ["id"], unique = true),
    ]
)
data class MessageChatEntity(
    @PrimaryKey
    val id: UUID = UUID.randomUUID(),
    var text: String,
    val senderId: UUID? = null,
    val receiverId: UUID? = null,
    val timestamp: Long = System.currentTimeMillis(),
    val status: MessageStatus = MessageStatus.SENT
)