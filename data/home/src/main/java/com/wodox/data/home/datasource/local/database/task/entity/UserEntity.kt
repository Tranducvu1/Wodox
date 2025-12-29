package com.wodox.data.home.datasource.local.database.task.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Date
import java.util.UUID

@Entity(
    tableName = "User",
    indices = [(Index(value = ["id"], unique = true))]
)
data class UserEntity(
    @PrimaryKey
    var id: UUID = UUID.randomUUID(),

    var name: String? = null,

    var email: String? = null,

    var avatar: String? = null,

    var isActive: Boolean,

    var password: Int = 0,

    var startAt: Date? = null,

    var dueAt: Date? = null,

    var estimateAt: Date? = null,

    var createdAt: Date = Date(),

    var updatedAt: Date = Date(),

    var deletedAt: Date? = null,
)