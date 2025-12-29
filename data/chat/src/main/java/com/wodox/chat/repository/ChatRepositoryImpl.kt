package com.wodox.chat.repository

import com.wodox.chat.dao.MessageDao
import com.wodox.chat.mapper.MessageChatMapper
import com.wodox.domain.chat.model.local.MessageChat
import com.wodox.domain.chat.repository.ChatRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject

class ChatRepositoryImpl @Inject constructor(
    private val messageDao: MessageDao,
    private val messageChatMapper: MessageChatMapper,
) : ChatRepository {

    override fun searchMessages(query: String): Flow<List<MessageChat>> {
        return messageDao.searchMessages("%$query%")
            .map { list ->
                list.map { messageChatMapper.mapToDomain(it) }
            }
    }

    override suspend fun sendMessage(message: MessageChat): MessageChat {
        val entity = messageChatMapper.mapToEntity(message)
        messageDao.insertMessage(entity)
        return messageChatMapper.mapToDomain(entity)
    }

    override suspend fun updateMessage(message: MessageChat) {
        val entity = messageChatMapper.mapToEntity(message)
        messageDao.updateMessage(entity)
    }

    override suspend fun deleteMessage(id: UUID) {
        messageDao.deleteMessageById(id)
    }

    override suspend fun clearMessages() {
        messageDao.deleteAllMessages()
    }

    override fun getConversationMessages(
        currentUserId: UUID,
        friendUserId: UUID
    ): Flow<List<MessageChat>> {
        return messageDao
            .getConversationMessages(currentUserId, friendUserId)
            .map { entities ->
                entities.map { entity ->
                    val domainMessage = messageChatMapper.mapToDomain(entity)
                    domainMessage.copy(
                        isCurrentUser = entity.senderId == currentUserId
                    )
                }
            }
    }
}