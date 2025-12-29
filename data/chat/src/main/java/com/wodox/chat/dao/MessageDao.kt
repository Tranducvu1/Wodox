package com.wodox.chat.dao

import androidx.room.*
import com.wodox.chat.model.MessageChatEntity
import kotlinx.coroutines.flow.Flow
import java.util.UUID

@Dao
interface MessageDao {
    @Query("SELECT * FROM messages WHERE text LIKE :query ORDER BY timestamp DESC")
    fun searchMessages(query: String): Flow<List<MessageChatEntity>>

    @Query("SELECT COUNT(*) FROM messages")
    fun getMessageCount(): Flow<Int>

    @Insert
    suspend fun insertMessage(message: MessageChatEntity): Long

    @Insert
    suspend fun insertMessages(messages: List<MessageChatEntity>): List<Long>

    @Update
    suspend fun updateMessage(message: MessageChatEntity)

    @Update
    suspend fun updateMessages(messages: List<MessageChatEntity>)

    @Query("DELETE FROM messages WHERE id = :id")
    suspend fun deleteMessageById(id: UUID)

    @Query("DELETE FROM messages")
    suspend fun deleteAllMessages()

    @Query(
        """
        SELECT * FROM messages 
        WHERE (senderId = :userId1 AND receiverId = :userId2)
           OR (senderId = :userId2 AND receiverId = :userId1)
        ORDER BY timestamp ASC
        """
    )
    fun getConversationMessages(
        userId1: UUID,
        userId2: UUID
    ): Flow<List<MessageChatEntity>>

    @Transaction
    suspend fun insertMessagesTransaction(messages: List<MessageChatEntity>) {
        insertMessages(messages)
    }
}