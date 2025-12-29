package com.wodox.domain.chat.usecase

import com.wodox.domain.base.BaseParamsFlowUnsafeUseCase
import com.wodox.domain.chat.model.local.MessageChat
import com.wodox.domain.chat.repository.ChatRepository
import kotlinx.coroutines.flow.Flow
import java.util.UUID
import javax.inject.Inject

class GetConversationMessagesUseCase @Inject constructor(
    private val chatRepository: ChatRepository
) : BaseParamsFlowUnsafeUseCase<Pair<UUID,UUID>, List<MessageChat>>() {
    override suspend fun execute(params: Pair<UUID, UUID>): Flow<List<MessageChat>> {
       return chatRepository.getConversationMessages(currentUserId = params.first, friendUserId = params.second)
    }
}