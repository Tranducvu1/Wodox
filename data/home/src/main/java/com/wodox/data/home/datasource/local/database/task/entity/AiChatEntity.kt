package com.wodox.data.home.datasource.local.database.task.entity


import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date
import java.util.UUID

@Entity(tableName = "ai_chats")
data class AiChatEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val taskId: String? = null,
    val userMessage: String,
    val aiResponse: String,
    val timestamp: Long = System.currentTimeMillis(),
    val createdAt: Date = Date()
)
