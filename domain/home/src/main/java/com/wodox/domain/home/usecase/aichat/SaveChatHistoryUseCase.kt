package com.wodox.domain.home.usecase.aichat

import com.wodox.domain.base.BaseParamsUnsafeUseCase
import com.wodox.domain.home.model.local.AiChat
import com.wodox.domain.home.repository.TaskRepository
import javax.inject.Inject

data class SaveChatHistoryParams(
    val userMessage: String,
    val aiResponse: String,
    val taskId: String? = null
)

class SaveChatHistoryUseCase @Inject constructor(
    private val taskRepository: TaskRepository
) : BaseParamsUnsafeUseCase<SaveChatHistoryParams, AiChat?>() {
    override suspend fun execute(params: SaveChatHistoryParams): AiChat? {
        return taskRepository.saveChatHistory(
            userMessage = params.userMessage,
            aiResponse = params.aiResponse,
            taskId = params.taskId
        )
    }
}
