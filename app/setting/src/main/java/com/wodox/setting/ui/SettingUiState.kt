package com.wodox.setting.ui

import com.wodox.setting.model.SettingSection

data class SettingUiState(
    val settingItems: List<SettingSection> = emptyList(),
    val showPromotionBanner: Boolean = true
)