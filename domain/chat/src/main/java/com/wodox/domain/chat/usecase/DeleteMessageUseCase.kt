package com.wodox.domain.chat.usecase

import com.wodox.domain.base.BaseParamsUnsafeUseCase
import com.wodox.domain.chat.repository.ChatRepository
import java.util.UUID
import javax.inject.Inject

class DeleteMessageUseCase @Inject constructor(
    private val chatRepository: ChatRepository
) : BaseParamsUnsafeUseCase<UUID, Unit>() {
    override suspend fun execute(params: UUID) {
        if (params.toString().isBlank()) {
            throw IllegalArgumentException("Message ID must be greater than 0")
        }
        chatRepository.deleteMessage(params)
    }
}