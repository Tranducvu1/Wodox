package com.wodox.chat.di

import com.wodox.chat.mapper.ChannelMapper
import com.wodox.chat.mapper.ChannelMemberMapper
import com.wodox.chat.mapper.ChannelMessageMapper
import com.wodox.chat.mapper.MessageChatMapper
import com.wodox.chat.mapper.NotificationMapper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ChatMapperModule {

    @Singleton
    @Provides
    fun providerNotificationMapper(
    ): NotificationMapper {
        return NotificationMapper()
    }

    @Singleton
    @Provides
    fun providerMessageChatMapper(
    ): MessageChatMapper {
        return MessageChatMapper()
    }

    @Singleton
    @Provides
    fun providerChannelMapper(
    ): ChannelMapper {
        return ChannelMapper()
    }

    @Singleton
    @Provides
    fun providerChannelMemberMapper(
    ): ChannelMemberMapper {
        return ChannelMemberMapper()
    }
    @Singleton
    @Provides
    fun providerChannelMessageMapper(
    ): ChannelMessageMapper {
        return ChannelMessageMapper()
    }


}