package com.wodox.domain.home.model.local.datasourece

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import com.wodox.core.extension.set

class SettingsPrefsDataSourceImpl @Inject constructor(
    context: Context
) : SettingsPrefsDataSource {

    private val sharedPrefs = context.getSharedPreferences(
        SHARED_PREFS_NAME,
        Context.MODE_PRIVATE
    )

    override val isNotificationEnabled: Flow<Boolean> = callbackFlow {
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            if (key == KEY_IS_NOTIFY_ENABLED) {
                trySend(sharedPrefs.getBoolean(KEY_IS_NOTIFY_ENABLED, false))
            }
        }

        sharedPrefs.registerOnSharedPreferenceChangeListener(listener)
        trySend(sharedPrefs.getBoolean(KEY_IS_NOTIFY_ENABLED, false))

        awaitClose {
            sharedPrefs.unregisterOnSharedPreferenceChangeListener(listener)
        }
    }

    override fun setNotificationEnabled(enabled: Boolean) {
        sharedPrefs.set(KEY_IS_NOTIFY_ENABLED, enabled)
    }

    companion object {
        private const val SHARED_PREFS_NAME = "App.Settings"
        private const val KEY_IS_NOTIFY_ENABLED = "IS_NOTIFY_ENABLED"
    }
}