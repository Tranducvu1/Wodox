package com.wodox.domain.home.model.local

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Date
import java.util.UUID

@Parcelize
data class Attachment(
    var id: UUID = UUID.randomUUID(),

    var taskId: UUID  = UUID.randomUUID(),

    var name: String? = null,

    var uri: String? = null,

    val type: AttachmentType? = null,

    var url: String? = null,

    var createdAt: Date = Date(),

    var updatedAt: Date = Date(),

    var deletedAt: Date? = null,
) : Parcelable