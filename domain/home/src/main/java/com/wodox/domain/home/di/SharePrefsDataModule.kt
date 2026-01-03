package com.wodox.domain.home.di

import android.content.Context
import com.wodox.domain.home.model.local.datasourece.SettingsPrefsDataSource
import com.wodox.domain.home.model.local.datasourece.SettingsPrefsDataSourceImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SharePrefsDataModule {

    @Provides
    @Singleton
    fun provideSettingSharePrefsDataSource(
        @ApplicationContext context: Context
    ): SettingsPrefsDataSource = SettingsPrefsDataSourceImpl(context)
}