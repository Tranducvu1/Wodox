package com.wodox.data.home.datasource.local.database.task.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Date
import java.util.UUID

@Entity(
    tableName = "Log",
    indices = [(Index(value = ["id"], unique = true))]
)
data class LogEntity(
    @PrimaryKey
    var id: UUID = UUID.randomUUID(),

    var taskId : UUID = UUID.randomUUID(),

    var title: String? = null,

    var description: String? = null,

    var createdAt: Date = Date(),

    var updatedAt: Date = Date(),

    var deletedAt: Date? = null,
)