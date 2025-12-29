package com.wodox.setting.model

import androidx.annotation.DrawableRes

data class SettingItem(
    @DrawableRes val icon: Int,
    val title: String,
    val type: SettingItemType,
    val badge: String? = null
)
