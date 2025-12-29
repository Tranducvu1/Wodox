package com.wodox.model

import java.util.UUID
data class Message(
    val id: UUID =UUID.randomUUID(),
    var text: String,
    val isUser: Boolean,
    val timestamp: Long = System.currentTimeMillis()

)
