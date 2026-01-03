package com.wodox.domain.home.repository


import kotlinx.coroutines.flow.Flow

interface SettingsRepository {

    val isNotificationEnabled: Flow<Boolean>

    fun setNotificationEnabled(enabled: Boolean)
}