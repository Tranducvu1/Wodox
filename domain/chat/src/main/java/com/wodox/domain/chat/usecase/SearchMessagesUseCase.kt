package com.wodox.domain.chat.usecase

import com.wodox.domain.base.BaseNoParamsFlowUseCase
import com.wodox.domain.base.BaseParamsFlowUnsafeUseCase
import com.wodox.domain.base.BaseParamsFlowUseCase
import com.wodox.domain.chat.model.local.MessageChat
import com.wodox.domain.chat.repository.ChatRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SearchMessagesUseCase @Inject constructor(
    private val chatRepository: ChatRepository
) : BaseParamsFlowUnsafeUseCase<String, List<MessageChat>>() {
    override suspend fun execute(params: String): Flow<List<MessageChat>> {
        if (params.isEmpty()) {
            throw IllegalArgumentException("Search query cannot be empty")
        }
        return chatRepository.searchMessages(params)
    }
}