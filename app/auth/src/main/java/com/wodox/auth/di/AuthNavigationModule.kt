package com.wodox.auth.di

import com.wodox.auth.navigation.AuthNavigatorImpl
import com.wodox.common.navigation.AuthNavigator
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AuthNavigationModule {
    @Provides
    @Singleton
    fun provideIntroNavigator(): AuthNavigator = AuthNavigatorImpl()
}