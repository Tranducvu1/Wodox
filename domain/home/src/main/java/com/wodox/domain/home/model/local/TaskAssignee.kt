package com.wodox.domain.home.model.local

import java.util.Date
import java.util.UUID

data class TaskAssignee(
    val id: UUID = UUID.randomUUID(),
    val taskId: UUID,
    val userId: UUID,
    var ownerId: UUID,
    val assignedAt: Date = Date(),
    var deletedAt: Date? = null
)