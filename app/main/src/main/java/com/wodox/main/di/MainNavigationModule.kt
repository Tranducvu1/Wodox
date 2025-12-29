package com.wodox.main.di

import com.wodox.common.navigation.MainNavigator
import com.wodox.main.navigation.MainNavigatorImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class MainNavigationModule {
    @Singleton
    @Provides
    fun provideMainNavigator(): MainNavigator = MainNavigatorImpl()
}
