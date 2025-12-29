package com.wodox.chat.di

import com.wodox.chat.dao.ChannelDao
import com.wodox.chat.dao.MessageDao
import com.wodox.chat.dao.NotificationDao
import com.wodox.chat.mapper.ChannelMapper
import com.wodox.chat.mapper.ChannelMemberMapper
import com.wodox.chat.mapper.ChannelMessageMapper
import com.wodox.chat.mapper.MessageChatMapper
import com.wodox.chat.mapper.NotificationMapper
import com.wodox.chat.repository.ChannelRepositoryImpl
import com.wodox.chat.repository.ChatRepositoryImpl
import com.wodox.chat.repository.NotificationRepositoryImpl
import com.wodox.domain.chat.repository.ChannelRepository
import com.wodox.domain.chat.repository.ChatRepository
import com.wodox.domain.chat.repository.NotificationRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ChatRepositoryModule {

    @Singleton
    @Provides
    fun provideNotificationRepository(
        mapper: NotificationMapper,
        dao: NotificationDao,
    ): NotificationRepository {
        return NotificationRepositoryImpl(
            dao = dao,
            mapper = mapper
        )
    }

    @Singleton
    @Provides
    fun provideChatRepository(
        mapper: MessageChatMapper,
        dao: MessageDao,
    ): ChatRepository {
        return ChatRepositoryImpl(
            messageChatMapper = mapper,
            messageDao = dao
        )
    }

    @Provides
    @Singleton
    fun provideChannelRepository(
        channelDao: ChannelDao,
        channelMapper: ChannelMapper,
        channelMemberMapper: ChannelMemberMapper,
        channelMessageMapper: ChannelMessageMapper
    ): ChannelRepository {
        return ChannelRepositoryImpl(
            channelDao,
            channelMapper,
            channelMemberMapper,
            channelMessageMapper
        )
    }
}

