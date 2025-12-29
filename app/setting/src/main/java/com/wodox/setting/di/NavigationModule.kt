package com.wodox.setting.di

import com.wodox.common.navigation.SettingNavigator
import com.wodox.setting.navigator.SettingNavigatorImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NavigationModule {

    @Provides
    @Singleton
    fun provideSettingNavigator(): SettingNavigator {
        return SettingNavigatorImpl()
    }
}