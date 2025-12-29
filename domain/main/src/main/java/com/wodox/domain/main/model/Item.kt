package com.wodox.domain.main.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Date
import java.util.UUID


@Parcelize
data class Item(
    var id: UUID = UUID.randomUUID(),

    var name: String? = null,

    var uri: String? = null,

    val type: ItemTypeProfile? = null,

    var createdAt: Date = Date(),

    var updatedAt: Date = Date(),

    var deletedAt: Date? = null,
) : Parcelable

fun getDefaultProfile(): List<Item> {
    return listOf(
        Item(
            name = "My calendar",
            type = ItemTypeProfile.MY_CALENDAR,
            uri = null
        ),
        Item(
            name = "Mute Notification",
            type = ItemTypeProfile.MUTE,
            uri = null
        ),
        Item(
            name = "Help and resources",
            type = ItemTypeProfile.HELP,
            uri = null
        ),
        Item(
            name = "Log out",
            type = ItemTypeProfile.SIGN_OUT,
            uri = null
        ),
    )
}
