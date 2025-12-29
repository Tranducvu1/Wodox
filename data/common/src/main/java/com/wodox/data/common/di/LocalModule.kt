package com.starnest.data.common.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.wodox.data.common.datasource.AppSharePrefs
import com.wodox.data.common.datasource.AppSharePrefsImpl
import com.wodox.core.data.model.SharePrefs
import com.wodox.core.data.util.GsonUtils
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LocalModule {
    @Singleton
    @Provides
    fun provideGson(): Gson {
        return GsonUtils.provideGson()
    }

    @Singleton
    @Provides
    fun providerSharedPreferences(app: Application): SharedPreferences {
        return app.getSharedPreferences(
            AppSharePrefsImpl.Keys.SHARED_PREFS_NAME, Context.MODE_PRIVATE
        )
    }

    @Singleton
    @Provides
    fun providerSharedPrefs(
        appSharePrefs: AppSharePrefs
    ): SharePrefs {
        return appSharePrefs
    }
}