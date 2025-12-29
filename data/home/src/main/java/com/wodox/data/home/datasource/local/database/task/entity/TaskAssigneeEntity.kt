package com.wodox.data.home.datasource.local.database.task.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Date
import java.util.UUID

@Entity(
    tableName = "TaskAssignees",
    foreignKeys = [
        ForeignKey(
            entity = TaskEntity::class,
            parentColumns = ["id"],
            childColumns = ["taskId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["userId"]),
        Index(value = ["taskId"])
    ]
)
data class TaskAssigneeEntity(
    @PrimaryKey
    var id: UUID = UUID.randomUUID(),

    var userId: UUID,

    var ownerId: UUID,

    var taskId: UUID,

    var assignedAt: Date = Date(),

    var deletedAt: Date? = null
)