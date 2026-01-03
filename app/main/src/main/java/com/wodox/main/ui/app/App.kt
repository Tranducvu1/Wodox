package com.wodox.main.ui.app

import android.content.SharedPreferences
import com.google.firebase.FirebaseApp
import com.wodox.common.app.BaseApp
import com.wodox.domain.home.repository.SettingsRepository
import com.wodox.notification.NotificationHelper
import com.wodox.ui.notification.manager.TaskDeadlineNotificationManager
import com.wodox.ui.notification.manager.TaskNotificationManager
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class App() : BaseApp() {
    override val isPremium: Boolean
        get() = false

    override val sharePrefs: SharedPreferences by lazy {
        getSharedPreferences("app_prefs", MODE_PRIVATE)
    }

    @Inject
    lateinit var settingsRepository: SettingsRepository

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)


    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        NotificationHelper.createChannel(this)
        TaskNotificationManager.createNotificationChannel(this)
        setupNotificationOnStartup()
    }

    fun reportGpt() {
    }


    private fun setupNotificationOnStartup() {
        applicationScope.launch(Dispatchers.IO) {
            val isEnabled = settingsRepository.isNotificationEnabled.first()
            if (isEnabled) {
                TaskDeadlineNotificationManager.startDeadlineCheck(this@App)
            }
        }
    }
}