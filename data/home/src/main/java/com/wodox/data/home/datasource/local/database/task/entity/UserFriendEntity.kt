package com.wodox.data.home.datasource.local.database.task.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Date

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
    var id: String = "",

    var userId: String = "",

    var friendId: String = "",

    var status: String = "",

    var createdAt: Date = Date(),

    var updatedAt: Date = Date(),

    var deletedAt: Date? = null
) {
    constructor() : this("", "", "", "", Date(), Date(), null)
}