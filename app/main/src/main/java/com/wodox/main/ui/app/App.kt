package com.wodox.main.ui.app

import android.content.SharedPreferences
import com.google.firebase.FirebaseApp
import com.wodox.common.app.BaseApp
import com.wodox.notification.NotificationHelper
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App() : BaseApp() {
    override val isPremium: Boolean
        get() = false

    override val sharePrefs: SharedPreferences by lazy {
        getSharedPreferences("app_prefs", MODE_PRIVATE)
    }

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        NotificationHelper.createChannel(this)
    }

    fun reportGpt() {
    }

}