package com.wodox.domain.home.usecase

import com.wodox.domain.base.BaseNoParamsFlowUnsafeUseCase
import com.wodox.domain.home.model.local.Task
import com.wodox.domain.home.repository.TaskRepository
import kotlinx.coroutines.flow.Flow

class GetTaskUseCase(
    private val repository: TaskRepository
) : BaseNoParamsFlowUnsafeUseCase<List<Task>>() {
    override suspend fun execute(): Flow<List<Task>> {
        return repository.getTask()
    }
}