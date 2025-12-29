package com.wodox.domain.home.usecase

import com.wodox.domain.base.BaseParamsUnsafeUseCase
import com.wodox.domain.home.model.local.Task
import com.wodox.domain.home.repository.TaskRepository
import javax.inject.Inject

class SaveTaskUseCase @Inject constructor(
    private val repository: TaskRepository
) : BaseParamsUnsafeUseCase<Task, Task?>() {
    override suspend fun execute(params: Task): Task? {
        return repository.save(params)
    }
}
