package com.wodox.domain.home.usecase.task

import com.wodox.domain.base.BaseParamsFlowUseCase
import com.wodox.domain.home.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject


class AskAIUseCase @Inject constructor(
    private val repository: TaskRepository
) : BaseParamsFlowUseCase<String, String?>() {
    override suspend fun execute(params: String): Flow<String?> = flow {
        val response = repository.askAI(params)
        emit(response)
    }
}