package com.wodox.core.app

import android.app.Application
import android.content.Context
import android.content.SharedPreferences

abstract class AbstractApplication : Application() {

    companion object {
        private var instance: AbstractApplication? = null

        fun getInstance(): AbstractApplication {
            return instance ?: throw RuntimeException("AbstractApplication not initialized")
        }

        fun getApplicationContext(): Context {
            return getInstance().applicationContext
        }
    }

    abstract val isPremium: Boolean

    abstract val sharePrefs: SharedPreferences

    override fun onCreate() {
        super.onCreate()
        instance = this
        initializeApp()
    }

    open fun initializeApp() {
        // Default implementation
    }

    open fun onAppDestroy() {

    }

    override fun onTerminate() {
        super.onTerminate()
        onAppDestroy()
    }
}