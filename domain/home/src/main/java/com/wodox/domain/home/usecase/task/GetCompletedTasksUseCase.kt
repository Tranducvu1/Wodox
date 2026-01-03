package com.wodox.domain.home.usecase.task

import com.wodox.domain.base.BaseNoParamsFlowUnsafeUseCase
import com.wodox.domain.home.model.local.Task
import com.wodox.domain.home.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCompletedTasksUseCase @Inject constructor(
    private val repository: TaskRepository
) : BaseNoParamsFlowUnsafeUseCase<List<Task>>() {
    override suspend fun execute(): Flow<List<Task>> {
        return repository.getCompletedTasks()
    }
}
