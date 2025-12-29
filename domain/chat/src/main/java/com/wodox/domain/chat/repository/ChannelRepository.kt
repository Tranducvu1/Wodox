package com.wodox.domain.chat.repository

import com.wodox.domain.chat.model.Channel
import com.wodox.domain.chat.model.ChannelMember
import com.wodox.domain.chat.model.ChannelMessage
import kotlinx.coroutines.flow.Flow
import java.util.UUID

interface ChannelRepository {
    // Channels
    fun getAllChannels(): Flow<List<Channel>>
    suspend fun getChannelById(channelId: UUID): Channel?
    fun getJoinedChannels(userId: UUID): Flow<List<Channel>>
    fun getMyChannels(userId: UUID): Flow<List<Channel>>
    suspend fun createChannel(channel: Channel): Channel
    suspend fun updateChannel(channel: Channel)
    suspend fun deleteChannel(channelId: UUID)
    suspend fun updateLastMessage(channelId: UUID, text: String, time: Long)
    suspend fun incrementUnreadCount(channelId: UUID)
    suspend fun clearUnreadCount(channelId: UUID)
    fun getAllChannels(userId: UUID? = null): Flow<List<Channel>>

    // Channel Members
    fun getChannelMembers(channelId: UUID): Flow<List<ChannelMember>>
    suspend fun addChannelMember(member: ChannelMember)
    suspend fun removeChannelMember(channelId: UUID, userId: UUID)
    suspend fun getChannelMember(channelId: UUID, userId: UUID): ChannelMember?

    // Channel Messages
    fun getChannelMessages(channelId: UUID): Flow<List<ChannelMessage>>
    suspend fun sendChannelMessage(message: ChannelMessage): ChannelMessage
    suspend fun deleteChannelMessage(message: ChannelMessage)
    suspend fun clearChannelMessages(channelId: UUID)
}