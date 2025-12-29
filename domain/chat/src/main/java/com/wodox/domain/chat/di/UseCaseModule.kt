package com.wodox.domain.chat.di

import com.wodox.domain.chat.repository.ChannelRepository
import com.wodox.domain.chat.repository.ChatRepository
import com.wodox.domain.chat.repository.NotificationRepository
import com.wodox.domain.chat.usecase.*
import com.wodox.domain.chat.usecase.channel.AddChannelMemberUseCase
import com.wodox.domain.chat.usecase.channel.ClearUnreadCountUseCase
import com.wodox.domain.chat.usecase.channel.CreateChannelUseCase
import com.wodox.domain.chat.usecase.channel.DeleteChannelUseCase
import com.wodox.domain.chat.usecase.channel.GetAllChannelsByIdUseCase
import com.wodox.domain.chat.usecase.channel.GetAllChannelsUseCase
import com.wodox.domain.chat.usecase.channel.GetChannelByIdUseCase
import com.wodox.domain.chat.usecase.channel.GetChannelMembersUseCase
import com.wodox.domain.chat.usecase.channel.GetChannelMessagesUseCase
import com.wodox.domain.chat.usecase.channel.GetJoinedChannelsUseCase
import com.wodox.domain.chat.usecase.channel.GetMyChannelsUseCase
import com.wodox.domain.chat.usecase.channel.JoinChannelUseCase
import com.wodox.domain.chat.usecase.channel.RemoveChannelMemberUseCase
import com.wodox.domain.chat.usecase.channel.SendChannelMessageUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    // ============= Notification UseCases =============
    @Singleton
    @Provides
    fun provideGetAllActivityUseCase(): GetAllActivityUseCase {
        return GetAllActivityUseCase()
    }

    @Singleton
    @Provides
    fun provideSaveNotificationUseCase(
        repository: NotificationRepository
    ): SaveNotificationUseCase {
        return SaveNotificationUseCase(repository)
    }

    @Singleton
    @Provides
    fun provideGetNotificationByUserIdUseCase(
        repository: NotificationRepository
    ): GetNotificationByUserIdUseCase {
        return GetNotificationByUserIdUseCase(repository)
    }

    @Singleton
    @Provides
    fun provideMarkAsReadNotificationUseCase(
        repository: NotificationRepository
    ): MarkAsReadNotificationUseCase {
        return MarkAsReadNotificationUseCase(repository)
    }

    @Singleton
    @Provides
    fun provideGetMarkTaskNotificationsReadUseCase(
        repository: NotificationRepository
    ): GetMarkTaskNotificationsReadUseCase {
        return GetMarkTaskNotificationsReadUseCase(repository)
    }

    @Singleton
    @Provides
    fun provideSendMessageUseCase(
        repository: ChatRepository
    ): SendMessageUseCase {
        return SendMessageUseCase(repository)
    }

    @Singleton
    @Provides
    fun provideDeleteMessageUseCase(
        repository: ChatRepository
    ): DeleteMessageUseCase {
        return DeleteMessageUseCase(repository)
    }

    @Singleton
    @Provides
    fun provideClearMessagesUseCase(
        repository: ChatRepository
    ): ClearMessagesUseCase {
        return ClearMessagesUseCase(repository)
    }

    @Singleton
    @Provides
    fun provideGetMessageByIdUseCase(
        repository: ChatRepository
    ): GetConversationMessagesUseCase {
        return GetConversationMessagesUseCase(repository)
    }

    @Singleton
    @Provides
    fun provideSearchMessagesUseCase(
        repository: ChatRepository
    ): SearchMessagesUseCase {
        return SearchMessagesUseCase(repository)
    }

    @Singleton
    @Provides
    fun provideUpdateMessageUseCase(
        repository: ChatRepository
    ): UpdateMessageUseCase {
        return UpdateMessageUseCase(repository)
    }

    @Singleton
    @Provides
    fun provideGetAllChannelsUseCase(
        repository: ChannelRepository
    ): GetAllChannelsUseCase {
        return GetAllChannelsUseCase(repository)
    }

    @Singleton
    @Provides
    fun provideGetJoinedChannelsUseCase(
        repository: ChannelRepository
    ): GetJoinedChannelsUseCase {
        return GetJoinedChannelsUseCase(repository)
    }

    @Singleton
    @Provides
    fun provideGetMyChannelsUseCase(
        repository: ChannelRepository
    ): GetMyChannelsUseCase {
        return GetMyChannelsUseCase(repository)
    }

    @Singleton
    @Provides
    fun provideCreateChannelUseCase(
        repository: ChannelRepository
    ): CreateChannelUseCase {
        return CreateChannelUseCase(repository)
    }

    @Singleton
    @Provides
    fun provideGetChannelByIdUseCase(
        repository: ChannelRepository
    ): GetChannelByIdUseCase {
        return GetChannelByIdUseCase(repository)
    }

    @Singleton
    @Provides
    fun provideDeleteChannelUseCase(
        repository: ChannelRepository
    ): DeleteChannelUseCase {
        return DeleteChannelUseCase(repository)
    }

    @Singleton
    @Provides
    fun provideGetAllChannelsByIdUseCase(
        repository: ChannelRepository
    ): GetAllChannelsByIdUseCase {
        return GetAllChannelsByIdUseCase(repository)
    }


    @Singleton
    @Provides
    fun provideGetChannelMessagesUseCase(
        repository: ChannelRepository
    ): GetChannelMessagesUseCase {
        return GetChannelMessagesUseCase(repository)
    }

    @Singleton
    @Provides
    fun provideSendChannelMessageUseCase(
        repository: ChannelRepository
    ): SendChannelMessageUseCase {
        return SendChannelMessageUseCase(repository)
    }

    @Singleton
    @Provides
    fun provideClearUnreadCountUseCase(
        repository: ChannelRepository
    ): ClearUnreadCountUseCase {
        return ClearUnreadCountUseCase(repository)
    }

    @Singleton
    @Provides
    fun provideGetChannelMembersUseCase(
        repository: ChannelRepository
    ): GetChannelMembersUseCase {
        return GetChannelMembersUseCase(repository)
    }

    @Singleton
    @Provides
    fun provideAddChannelMemberUseCase(
        repository: ChannelRepository
    ): AddChannelMemberUseCase {
        return AddChannelMemberUseCase(repository)
    }

    @Singleton
    @Provides
    fun provideRemoveChannelMemberUseCase(
        repository: ChannelRepository
    ): RemoveChannelMemberUseCase {
        return RemoveChannelMemberUseCase(repository)
    }

    @Singleton
    @Provides
    fun provideJoinChannelUseCase(
        repository: ChannelRepository
    ): JoinChannelUseCase {
        return JoinChannelUseCase(repository)
    }
}