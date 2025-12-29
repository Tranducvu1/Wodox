package com.wodox.domain.home.model.local

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
enum class TaskStatus : Parcelable {
    TODO,
    IN_PROGRESS,
    DONE,
    BLOCKED
}