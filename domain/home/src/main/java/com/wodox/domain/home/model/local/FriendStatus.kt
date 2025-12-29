package com.wodox.domain.home.model.local

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
enum class FriendStatus : Parcelable {
    PENDING,
    ACCEPTED,
    REJECTED,
    BLOCKED
}
