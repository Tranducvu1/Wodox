package com.wodox.domain.chat.usecase

import com.wodox.domain.base.BaseNoParamsUnsafeUseCase
import com.wodox.domain.chat.repository.ChatRepository
import javax.inject.Inject

class ClearMessagesUseCase @Inject constructor(
    private val chatRepository: ChatRepository
) : BaseNoParamsUnsafeUseCase<Unit>() {
    override suspend fun execute() {
        chatRepository.clearMessages()
    }
}