package com.wodox.data.home.datasource.local.database.task.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.wodox.domain.home.model.local.TaskStatus
import java.util.Date
import java.util.UUID

@Entity(
    tableName = "Task",
    indices = [Index(value = ["id"], unique = true)]
)
data class TaskEntity(
    @PrimaryKey
    val id: UUID = UUID.randomUUID(),
    val projectId: UUID? = null,
    val ownerId: UUID,
    val title: String,
    val description: String? = null,
    val status: TaskStatus = TaskStatus.TODO,
    val priority: Int,
    val difficulty: Int,
    val support: Int,
    val startAt: Date? = null,
    val dueAt: Date? = null,
    val assignedUserIds: String = "",
    val createdAt: Date = Date(),
    var updatedAt: Date = Date(),
    var deletedAt: Date? = null,
    val isFavourite: Boolean = false
)