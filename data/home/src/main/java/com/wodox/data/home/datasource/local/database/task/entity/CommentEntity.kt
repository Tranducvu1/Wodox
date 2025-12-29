package com.wodox.data.home.datasource.local.database.task.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Date
import java.util.UUID

@Entity(
    tableName = "Comment",
    indices = [(Index(value = ["id"], unique = true)), (Index(value = ["taskId"]))]
)
data class CommentEntity(
    @PrimaryKey
    var id: UUID = UUID.randomUUID(),

    var taskId: UUID = UUID.randomUUID(),

    var content: String = "",

    var userId: UUID? = null,

    var userName: String? = null,

    var createdAt: Date = Date(),

    var updatedAt: Date = Date(),

    var deletedAt: Date? = null,
)