package com.wodox.chat.dao


import androidx.room.*
import com.wodox.chat.model.ChannelEntity
import com.wodox.chat.model.ChannelMemberEntity
import com.wodox.chat.model.ChannelMessageEntity

import kotlinx.coroutines.flow.Flow

@Dao
interface ChannelDao {

    // Channels
    @Query("SELECT * FROM channels ORDER BY lastMessageTime DESC")
    fun getAllChannels(): Flow<List<ChannelEntity>>

    @Query("SELECT * FROM channels WHERE id = :channelId")
    suspend fun getChannelById(channelId: String): ChannelEntity?

    @Query("SELECT c.* FROM channels c INNER JOIN channel_members cm ON c.id = cm.channelId WHERE cm.userId = :userId ORDER BY c.lastMessageTime DESC")
    fun getJoinedChannels(userId: String): Flow<List<ChannelEntity>>

    @Query("SELECT * FROM channels WHERE creatorId = :userId ORDER BY createdAt DESC")
    fun getMyChannels(userId: String): Flow<List<ChannelEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChannel(channel: ChannelEntity): Long

    @Update
    suspend fun updateChannel(channel: ChannelEntity)

    @Delete
    suspend fun deleteChannel(channel: ChannelEntity)

    @Query("DELETE FROM channels WHERE id = :channelId")
    suspend fun deleteChannelById(channelId: String)

    @Query("UPDATE channels SET lastMessageText = :text, lastMessageTime = :time WHERE id = :channelId")
    suspend fun updateLastMessage(channelId: String, text: String, time: Long)

    @Query("UPDATE channels SET unreadCount = unreadCount + 1 WHERE id = :channelId")
    suspend fun incrementUnreadCount(channelId: String)

    @Query("UPDATE channels SET unreadCount = 0 WHERE id = :channelId")
    suspend fun clearUnreadCount(channelId: String)

    @Query("SELECT * FROM channel_members WHERE channelId = :channelId")
    fun getChannelMembers(channelId: String): Flow<List<ChannelMemberEntity>>

    @Query("SELECT * FROM channel_members WHERE channelId = :channelId AND userId = :userId")
    suspend fun getChannelMember(channelId: String, userId: String): ChannelMemberEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChannelMember(member: ChannelMemberEntity)

    @Delete
    suspend fun deleteChannelMember(member: ChannelMemberEntity)

    @Query("DELETE FROM channel_members WHERE channelId = :channelId AND userId = :userId")
    suspend fun removeChannelMember(channelId: String, userId: String)


    @Query("""
        SELECT c.*, 
               CASE WHEN cm.userId IS NOT NULL THEN 1 ELSE 0 END as isJoined
        FROM channels c 
        LEFT JOIN channel_members cm ON c.id = cm.channelId AND cm.userId = :userId 
        ORDER BY c.lastMessageTime DESC
    """)
    fun getAllChannelsWithJoinStatus(userId: String): Flow<List<ChannelEntity>>


    @Query("SELECT * FROM channel_messages WHERE channelId = :channelId ORDER BY timestamp ASC")
    fun getChannelMessages(channelId: String): Flow<List<ChannelMessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChannelMessage(message: ChannelMessageEntity): Long

    @Delete
    suspend fun deleteChannelMessage(message: ChannelMessageEntity)

    @Query("DELETE FROM channel_messages WHERE channelId = :channelId")
    suspend fun clearChannelMessages(channelId: String)
}