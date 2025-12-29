package com.wodox.domain.chat.usecase

import com.wodox.domain.base.BaseParamsUnsafeUseCase
import com.wodox.domain.chat.model.local.MessageChat
import com.wodox.domain.chat.repository.ChatRepository
import javax.inject.Inject

class UpdateMessageUseCase @Inject constructor(
    private val chatRepository: ChatRepository
) : BaseParamsUnsafeUseCase<MessageChat, Unit>() {
    override suspend fun execute(params: MessageChat) {
        if (params.text.isBlank()) {
            throw IllegalArgumentException("Message ID cannot be 0")
        }
        if (params.text.isEmpty()) {
            throw IllegalArgumentException("Message text cannot be empty")
        }
        chatRepository.updateMessage(params)
    }
}