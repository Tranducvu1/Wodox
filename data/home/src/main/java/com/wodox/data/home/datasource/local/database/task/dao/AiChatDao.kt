package com.wodox.data.home.datasource.local.database.task.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.wodox.data.home.datasource.local.database.task.entity.AiChatEntity
import kotlinx.coroutines.flow.Flow
import java.util.UUID

@Dao
interface AiChatDao {

    @Insert
    suspend fun insert(chat: AiChatEntity)

    @Insert
    suspend fun insertAll(chats: List<AiChatEntity>)

    @Query("SELECT * FROM ai_chats WHERE taskId = :taskId ORDER BY timestamp DESC")
    fun getChatsByTaskId(taskId: String): Flow<List<AiChatEntity>>

    @Query("SELECT * FROM ai_chats ORDER BY timestamp DESC LIMIT :limit")
    fun getRecentChats(limit: Int = 50): Flow<List<AiChatEntity>>

    @Query("SELECT * FROM ai_chats WHERE id = :chatId")
    suspend fun getChatById(chatId: String): AiChatEntity?

    @Delete
    suspend fun delete(chat: AiChatEntity)

    @Query("DELETE FROM ai_chats WHERE taskId = :taskId")
    suspend fun deleteByTaskId(taskId: String)

    @Query("DELETE FROM ai_chats")
    suspend fun deleteAll()
}