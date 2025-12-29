package com.wodox.domain.home.usecase

import com.wodox.domain.base.BaseParamsUnsafeUseCase
import com.wodox.domain.home.model.local.Task
import com.wodox.domain.home.repository.TaskRepository
import java.util.UUID
import javax.inject.Inject


class GetTaskByTaskIdUseCase @Inject constructor(
    private val userRepository: TaskRepository
) : BaseParamsUnsafeUseCase<UUID, Task?>() {
    override suspend fun execute(params: UUID): Task? {
        return userRepository.getTaskByTaskID(params)
    }
}
