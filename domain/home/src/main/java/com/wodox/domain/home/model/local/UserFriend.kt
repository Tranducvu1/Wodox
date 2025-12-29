package com.wodox.domain.home.model.local

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.io.Serializable
import java.util.Date
import java.util.UUID

@Parcelize
data class UserFriend(
    var id: UUID = UUID.randomUUID(),

    var userId: UUID,

    var friendId: UUID,

    var status: FriendStatus = FriendStatus.PENDING,

    var createdAt: Date = Date(),

    var updatedAt: Date = Date(),

    var deletedAt: Date? = null
) : Parcelable, Serializable