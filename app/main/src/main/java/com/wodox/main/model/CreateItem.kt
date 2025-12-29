package com.wodox.main.model


import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

data class CreateItem(
    val id: Int,
    val type: CreateType,
    @StringRes val title: Int,
    @StringRes val description: Int,
    @DrawableRes val icon: Int,
    @ColorRes val backgroundColor: Int
)

enum class CreateType {
    LIST,
    SPACE,
    CHAT_MESSAGE,
    CHANNEL,
    TASK,
    REMINDER,
    TRACK_TIME,
    DOC,
    NOTE
}