package com.wodox.domain.chat.usecase
import com.wodox.domain.base.BaseParamsUnsafeUseCase
import com.wodox.domain.chat.model.local.MessageChat
import com.wodox.domain.chat.repository.ChatRepository
import javax.inject.Inject

class SendMessageUseCase @Inject constructor(
    private val chatRepository: ChatRepository
) : BaseParamsUnsafeUseCase<MessageChat, MessageChat>() {
    override suspend fun execute(params: MessageChat): MessageChat {
        return chatRepository.sendMessage(params)
    }
}