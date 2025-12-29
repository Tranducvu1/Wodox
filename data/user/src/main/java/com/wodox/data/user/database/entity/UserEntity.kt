package com.wodox.data.user.database.entity

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
    var id: String = UUID.randomUUID().toString(),
    var email: String = "",
    var password: String = "",
    var name: String = "",
    var avatar: String = "",
    var isActive: Boolean = false,
    var createdAt: Date = Date(),
    var updatedAt: Date = Date(),
    var deletedAt: Date? = null,
)