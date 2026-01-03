package com.wodox.data.home.repository

import com.wodox.domain.home.model.local.datasourece.SettingsPrefsDataSource
import com.wodox.domain.home.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SettingsRepositoryImpl @Inject constructor(
    private val localDataSource: SettingsPrefsDataSource,
) : SettingsRepository {
    override val isNotificationEnabled: Flow<Boolean> = localDataSource.isNotificationEnabled

    override fun setNotificationEnabled(enabled: Boolean) {
        localDataSource.setNotificationEnabled(enabled)
    }

}