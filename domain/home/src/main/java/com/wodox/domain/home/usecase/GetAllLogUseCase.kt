package com.wodox.domain.home.usecase

import com.wodox.domain.base.BaseParamsFlowUnsafeUseCase
import com.wodox.domain.home.model.local.Log
import com.wodox.domain.home.repository.LogRepository
import kotlinx.coroutines.flow.Flow
import java.util.UUID

class GetAllLogUseCase(
    private val repository: LogRepository
) : BaseParamsFlowUnsafeUseCase<UUID, List<Log>>() {
    override suspend fun execute(params: UUID): Flow<List<Log>> {
        return repository.getAllLog(params)
    }
}