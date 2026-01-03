package com.wodox.chat.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "Channels")
data class ChannelEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val name: String = "",
    val description: String? = null,
    val iconUrl: String? = null,
    val creatorId: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val memberCount: Int = 0,
    val isPrivate: Boolean = false,
    val lastMessageText: String? = null,
    val lastMessageTime: Long? = null,
    val unreadCount: Int = 0,
    val isJoined: Boolean = false
)