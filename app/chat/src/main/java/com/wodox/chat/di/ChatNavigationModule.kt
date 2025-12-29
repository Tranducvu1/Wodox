package com.wodox.chat.di

import com.wodox.chat.navigtion.ChatNavigatorImpl
import com.wodox.common.navigation.ChatNavigator
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ChatNavigationModule {
    @Provides
    @Singleton
    fun provideChatNavigator(): ChatNavigator = ChatNavigatorImpl()
}