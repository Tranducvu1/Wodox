package com.wodox.domain.home.model.local.datasourece

import kotlinx.coroutines.flow.Flow

interface SettingsPrefsDataSource {
    val isNotificationEnabled: Flow<Boolean>

    fun setNotificationEnabled(enabled: Boolean)
}