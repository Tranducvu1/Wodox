package com.wodox.chat.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "Channel_members")
data class ChannelMemberEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val channelId: String,
    val userId: String,
    val role: String = "MEMBER",
    val joinedAt: Long = System.currentTimeMillis()
)
