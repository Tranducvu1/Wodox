package com.wodox.domain.home.usecase

import androidx.paging.PagingData
import com.wodox.domain.base.BaseParamsFlowUnsafeUseCase
import com.wodox.domain.home.model.local.Task
import com.wodox.domain.home.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import java.util.UUID

class GetAllTaskUseCase(
    private val repository: TaskRepository
) : BaseParamsFlowUnsafeUseCase<UUID,PagingData<Task>>() {
    override suspend fun execute(params: UUID): Flow<PagingData<Task>> {
        return repository.getAllTaskByUserID(params)
    }
}