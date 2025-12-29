package com.wodox.domain.home.usecase.aichat

import com.wodox.domain.base.BaseParamsUnsafeUseCase
import com.wodox.domain.home.repository.TaskRepository
import javax.inject.Inject

class DeleteAllChatsByTaskUseCase @Inject constructor(
    private val taskRepository: TaskRepository
) : BaseParamsUnsafeUseCase<String, Unit>() {

    override suspend fun execute(params: String) {
        taskRepository.deleteChatsByTaskId(params)
    }
}