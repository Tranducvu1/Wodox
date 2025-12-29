package com.wodox.domain.home.model.local

import java.io.Serializable
import java.util.Date
import java.util.UUID

data class SubTask(
    var id: UUID = UUID.randomUUID(),

    var taskId: UUID,

    var title: String = "",

    var description: String? = null,

    var status: TaskStatus = TaskStatus.TODO,

    var priority: Int = 0,

    var startAt: Date? = null,

    var dueAt: Date? = null,

    var estimateAt: Date? = null,

    var createdAt: Date = Date(),

    var updatedAt: Date = Date(),

    var deletedAt: Date? = null,
): Serializable