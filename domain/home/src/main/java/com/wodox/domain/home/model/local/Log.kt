package com.wodox.domain.home.model.local

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Date
import java.util.UUID

@Parcelize
data class  Log(
    var id: UUID = UUID.randomUUID(),

    var taskId : UUID = UUID.randomUUID(),

    var title: String? = null,

    var description: String? = null,

    var createdAt: Date = Date(),

    var updatedAt: Date = Date(),

    var deletedAt: Date? = null,
) : Parcelable