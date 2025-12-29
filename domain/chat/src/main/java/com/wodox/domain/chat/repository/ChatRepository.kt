package com.wodox.domain.chat.repository

import com.wodox.domain.chat.model.local.MessageChat
import kotlinx.coroutines.flow.Flow
import java.util.UUID

interface ChatRepository {
    fun searchMessages(query: String): Flow<List<MessageChat>>

    suspend fun sendMessage(message: MessageChat): MessageChat

    suspend fun updateMessage(message: MessageChat)

    suspend fun deleteMessage(id: UUID)

    suspend fun clearMessages()

    fun getConversationMessages(
        currentUserId: UUID,
        friendUserId: UUID
    ): Flow<List<MessageChat>>
}