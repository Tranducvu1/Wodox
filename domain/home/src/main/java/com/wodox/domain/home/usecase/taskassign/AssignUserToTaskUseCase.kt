package com.wodox.domain.home.usecase.taskassign

import com.wodox.domain.base.BaseParamsUnsafeUseCase
import com.wodox.domain.home.model.local.TaskAssignee
import com.wodox.domain.home.repository.TaskAssignRepository
import javax.inject.Inject

class AssignUserToTaskUseCase @Inject constructor(
    private val repository: TaskAssignRepository
) : BaseParamsUnsafeUseCase<TaskAssignee, Unit>() {
    override suspend fun execute(params: TaskAssignee) {
        repository.assignUserToTask(params)
    }
}