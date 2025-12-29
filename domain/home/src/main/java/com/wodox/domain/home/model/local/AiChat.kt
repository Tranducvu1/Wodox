package com.wodox.domain.home.model.local

import java.util.Date
import java.util.UUID

data class AiChat(
    val id: String = UUID.randomUUID().toString(),
    val taskId: String? = null,
    val userMessage: String,
    val aiResponse: String,
    val timestamp: Long = System.currentTimeMillis(),
    val createdAt: Date = Date()
)
