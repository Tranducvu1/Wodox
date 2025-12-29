package com.wodox.setting.ui

sealed class SettingUiAction {
    object LoadSettings : SettingUiAction()
    object ClaimPromotion : SettingUiAction()
}