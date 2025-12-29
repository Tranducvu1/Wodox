package com.wodox.domain.chat.model

import android.os.Parcelable
import com.wodox.domain.home.model.local.FriendStatus
import com.wodox.domain.user.model.User
import java.io.Serializable
import java.util.UUID
import kotlinx.parcelize.Parcelize


@Parcelize
data class UserWithFriendStatus(
    val user: User,
    val status: FriendStatus? = null,
    val relationUserId: UUID? = null,
    val relationFriendId: UUID? = null,
    val currentUserId: UUID? = null
): Serializable, Parcelable

