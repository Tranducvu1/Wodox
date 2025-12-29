package com.wodox.data.home.datasource.local.database.task.entity

import com.wodox.domain.home.model.local.FriendStatus


import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Date
import java.util.UUID

@Entity(
    tableName = "UserFriend",
    indices = [
        Index(value = ["id"], unique = true),
        Index(value = ["userId"]),
        Index(value = ["friendId"])
    ]
)
data class UserFriendEntity(
    @PrimaryKey
    var id: UUID = UUID.randomUUID(),

    var userId: UUID,

    var friendId: UUID,

    var status: FriendStatus = FriendStatus.PENDING,

    var createdAt: Date = Date(),

    var updatedAt: Date = Date(),

    var deletedAt: Date? = null
)
