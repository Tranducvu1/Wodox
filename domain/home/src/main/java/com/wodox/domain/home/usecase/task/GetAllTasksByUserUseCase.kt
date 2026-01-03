package com.wodox.domain.home.usecase.task

import com.wodox.domain.base.BaseParamsUnsafeUseCase
import com.wodox.domain.home.model.local.Task
import com.wodox.domain.home.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import java.util.UUID
import javax.inject.Inject

class GetAllTasksByUserUseCase @Inject constructor(
    private val taskRepository: TaskRepository
) : BaseParamsUnsafeUseCase<UUID, Flow<List<Task>>>() {
    override suspend fun execute(params: UUID): Flow<List<Task>> {
        return taskRepository.getAllTasksByUserId(params)
    }
}