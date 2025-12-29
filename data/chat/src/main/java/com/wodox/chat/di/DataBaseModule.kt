package com.wodox.chat.di

import android.content.Context
import com.wodox.chat.ChatDatabase
import com.wodox.chat.dao.ChannelDao
import com.wodox.chat.dao.MessageDao
import com.wodox.chat.dao.NotificationDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ChatDatabaseModule {
    @Singleton
    @Provides
    fun provideTaskDatabase(
        @ApplicationContext app: Context,
    ) = ChatDatabase.getInstance(app)

    @Provides
    fun provideNotificationDao(database: ChatDatabase): NotificationDao = database.notificationDao()

    @Provides
    fun provideMessageDao(database: ChatDatabase): MessageDao = database.messageDao()

    @Provides
    fun provideChannelDao(database: ChatDatabase): ChannelDao = database.channelDao()
}