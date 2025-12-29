package com.wodox.intro.di

import com.wodox.common.navigation.IntroNavigator
import com.wodox.intro.navigation.IntroNavigatorImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object IntroNavigationModule {
    @Provides
    @Singleton
    fun provideIntroNavigator(): IntroNavigator = IntroNavigatorImpl()

}