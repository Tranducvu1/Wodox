package com.wodox.domain.home.usecase.aichat

import com.wodox.domain.base.BaseParamsUnsafeUseCase
import com.wodox.domain.home.model.local.AiChat
import com.wodox.domain.home.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetChatsByTaskIdUseCase @Inject constructor(
    private val taskRepository: TaskRepository
) : BaseParamsUnsafeUseCase<String, Flow<List<AiChat>>>() {

    override suspend fun execute(params: String): Flow<List<AiChat>> {
        return taskRepository.getChatsByTaskId(params)
    }
}