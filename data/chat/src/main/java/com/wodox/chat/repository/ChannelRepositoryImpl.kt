package com.wodox.chat.repository


import com.wodox.chat.dao.ChannelDao
import com.wodox.chat.mapper.ChannelMapper
import com.wodox.chat.mapper.ChannelMemberMapper
import com.wodox.chat.mapper.ChannelMessageMapper
import com.wodox.domain.chat.model.Channel
import com.wodox.domain.chat.model.ChannelMember
import com.wodox.domain.chat.model.ChannelMessage
import com.wodox.domain.chat.model.ChannelRole
import com.wodox.domain.chat.repository.ChannelRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject

class ChannelRepositoryImpl @Inject constructor(
    private val channelDao: ChannelDao,
    private val channelMapper: ChannelMapper,
    private val channelMemberMapper: ChannelMemberMapper,
    private val channelMessageMapper: ChannelMessageMapper
) : ChannelRepository {
    override fun getAllChannels(): Flow<List<Channel>> {
        return channelDao.getAllChannels().map { entities ->
            channelMapper.mapToDomainList(entities)
        }
    }

    override suspend fun getChannelById(channelId: UUID): Channel? {
        return channelDao.getChannelById(channelId.toString())?.let {
            channelMapper.mapToDomain(it)
        }
    }

    override fun getJoinedChannels(userId: UUID): Flow<List<Channel>> {
        android.util.Log.d("ChannelRepository", "getJoinedChannels called with userId: $userId")
        return channelDao.getJoinedChannels(userId.toString())
            .map { entities ->
                android.util.Log.d("ChannelRepository", "Raw entities from DAO: ${entities.size}")
                entities.forEach { entity ->
                    android.util.Log.d("ChannelRepository", "- Entity: id=${entity.id}, name=${entity.name}")
                }

                val channels = channelMapper.mapToDomainList(entities)
                android.util.Log.d("ChannelRepository", "Mapped to ${channels.size} domain channels")
                channels.forEach { channel ->
                    android.util.Log.d("ChannelRepository", "- Channel: id=${channel.id}, name=${channel.name}, isJoined=${channel.isJoined}")
                }

                channels
            }
    }

    override fun getMyChannels(userId: UUID): Flow<List<Channel>> {
        return channelDao.getMyChannels(userId.toString()).map { entities ->
            channelMapper.mapToDomainList(entities)
        }
    }

    override suspend fun createChannel(channel: Channel): Channel {
        val entity = channelMapper.mapToEntity(channel)
        channelDao.insertChannel(entity)
        val ownerMember = ChannelMember(
            channelId = channel.id,
            userId = channel.creatorId,
            role = ChannelRole.OWNER
        )
        channelDao.insertChannelMember(channelMemberMapper.mapToEntity(ownerMember))

        return channel
    }

    override suspend fun updateChannel(channel: Channel) {
        channelDao.updateChannel(channelMapper.mapToEntity(channel))
    }

    override suspend fun deleteChannel(channelId: UUID) {
        channelDao.deleteChannelById(channelId.toString())
    }

    override suspend fun updateLastMessage(channelId: UUID, text: String, time: Long) {
        channelDao.updateLastMessage(channelId.toString(), text, time)
    }

    override suspend fun incrementUnreadCount(channelId: UUID) {
        channelDao.incrementUnreadCount(channelId.toString())
    }

    override suspend fun clearUnreadCount(channelId: UUID) {
        channelDao.clearUnreadCount(channelId.toString())
    }

    // Channel Members
    override fun getChannelMembers(channelId: UUID): Flow<List<ChannelMember>> {
        return channelDao.getChannelMembers(channelId.toString()).map { entities ->
            channelMemberMapper.mapToDomainList(entities)
        }
    }

    override suspend fun addChannelMember(member: ChannelMember) {
        channelDao.insertChannelMember(channelMemberMapper.mapToEntity(member))
        val channel = channelDao.getChannelById(member.channelId.toString())
        if (channel != null) {
            val updatedChannel = channel.copy(memberCount = channel.memberCount + 1)
            channelDao.updateChannel(updatedChannel)
        }
    }

    override fun getAllChannels(userId: UUID?): Flow<List<Channel>> {
        return if (userId != null) {
            channelDao.getAllChannelsWithJoinStatus(userId.toString()).map { entities ->
                channelMapper.mapToDomainList(entities)
            }
        } else {
            channelDao.getAllChannels().map { entities ->
                channelMapper.mapToDomainList(entities)
            }
        }
    }

    override suspend fun removeChannelMember(channelId: UUID, userId: UUID) {
        channelDao.removeChannelMember(channelId.toString(), userId.toString())
        val channel = channelDao.getChannelById(channelId.toString())
        if (channel != null) {
            val updatedChannel = channel.copy(memberCount = maxOf(0, channel.memberCount - 1))
            channelDao.updateChannel(updatedChannel)
        }
    }

    override suspend fun getChannelMember(channelId: UUID, userId: UUID): ChannelMember? {
        return channelDao.getChannelMember(channelId.toString(), userId.toString())?.let {
            channelMemberMapper.mapToDomain(it)
        }
    }

    override fun getChannelMessages(channelId: UUID): Flow<List<ChannelMessage>> {
        return channelDao.getChannelMessages(channelId.toString()).map { entities ->
            channelMessageMapper.mapToDomainList(entities)
        }
    }

    override suspend fun sendChannelMessage(message: ChannelMessage): ChannelMessage {
        channelDao.insertChannelMessage(channelMessageMapper.mapToEntity(message))
        channelDao.updateLastMessage(
            message.channelId.toString(),
            message.text,
            message.timestamp
        )

        return message
    }

    override suspend fun deleteChannelMessage(message: ChannelMessage) {
        channelDao.deleteChannelMessage(channelMessageMapper.mapToEntity(message))
    }

    override suspend fun clearChannelMessages(channelId: UUID) {
        channelDao.clearChannelMessages(channelId.toString())
    }
}