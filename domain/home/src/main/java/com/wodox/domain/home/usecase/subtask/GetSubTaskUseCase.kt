package com.wodox.domain.home.usecase.subtask

import com.wodox.domain.base.BaseNoParamsFlowUnsafeUseCase
import com.wodox.domain.base.BaseParamsFlowUnsafeUseCase
import com.wodox.domain.home.model.local.SubTask
import com.wodox.domain.home.repository.SubTaskRepository
import kotlinx.coroutines.flow.Flow
import java.util.UUID


class GetAllSubTaskByTaskIdUseCase(
    private val repository: SubTaskRepository
) : BaseParamsFlowUnsafeUseCase<UUID, List<SubTask>>() {
    override suspend fun execute(params: UUID): Flow<List<SubTask>> {
        return repository.getAllSubTaskByTaskID(params)
    }
}
