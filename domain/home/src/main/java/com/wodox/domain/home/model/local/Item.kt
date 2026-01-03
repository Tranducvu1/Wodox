package com.wodox.domain.home.model.local

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Date
import java.util.UUID

@Parcelize
data class Item(
    var id: UUID = UUID.randomUUID(),

    var name: String? = null,

    var uri: String? = null,

    val type: ItemType? = null,

    var createdAt: Date = Date(),

    var updatedAt: Date = Date(),

    var deletedAt: Date? = null,
) : Parcelable

fun getDefaultItems(): List<Item> {
    return listOf(
        Item(
            name = "Channel", type = ItemType.CHANNEL, uri = null
        ),
        Item(
            name = "Task", type = ItemType.TASK, uri = null
        ),
        Item(
            name = "Reminder", type = ItemType.REMINDER, uri = null
        ),
        Item(
            name = "Document", type = ItemType.DOC, uri = null
        ),
    )
}




