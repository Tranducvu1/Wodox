package com.wodox.docs.di


import com.wodox.common.navigation.DocNavigator
import com.wodox.docs.navigation.DocsNavigatorImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DocNavigationModule {
    @Provides
    @Singleton
    fun provideChatNavigator(): DocNavigator = DocsNavigatorImpl()
}