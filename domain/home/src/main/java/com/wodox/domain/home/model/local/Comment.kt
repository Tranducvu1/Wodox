package com.wodox.domain.home.model.local


import java.util.Date
import java.util.UUID

data class Comment(
    val id: UUID = UUID.randomUUID(),

    val taskId: UUID = UUID.randomUUID(),

    val content: String = "",

    val userId: UUID? = null,

    val userName: String? = null,

    val createdAt: Date = Date(),

    val updatedAt: Date = Date(),

    val deletedAt: Date? = null,
)
