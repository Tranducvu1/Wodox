package com.wodox.domain.home.usecase

import com.wodox.domain.base.BaseParamsUnsafeUseCase
import com.wodox.domain.home.model.local.TaskAnalysisResult
import com.wodox.domain.home.repository.TaskRepository
import java.util.UUID
import javax.inject.Inject

class AnalyzeUserTasksUseCase @Inject constructor(
    private val repository: TaskRepository
) : BaseParamsUnsafeUseCase<UUID, TaskAnalysisResult?>() {
    override suspend fun execute(params: UUID): TaskAnalysisResult? {
        return repository.analyzeUserTasks(params)
    }
}