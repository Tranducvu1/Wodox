package com.wodox.domain.home.model.local

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Date
import java.util.UUID

@Parcelize
data class MenuOption(
    var id: UUID = UUID.randomUUID(),

    var name: String? = null,

    var uri: String? = null,

    val type: ItemMenu? = null,

    var createdAt: Date = Date(),

    var updatedAt: Date = Date(),

    var deletedAt: Date? = null,
) : Parcelable

fun getDefaultItemsMenu(): List<MenuOption> {
    return listOf(
        MenuOption(
            name = "Share",
            type = ItemMenu.SHARE,
            uri = null
        ),
        MenuOption(
            name = "Duplicate",
            type = ItemMenu.DUPLICATE,
            uri = null
        ),
        MenuOption(
            name = "Remind me",
            type = ItemMenu.REMIND,
            uri = null
        ),
        MenuOption(
            name = "Delete",
            type = ItemMenu.DELETE,
            uri = null
        ),
    )
}