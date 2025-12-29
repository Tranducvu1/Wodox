package com.wodox.domain.home.usecase.taskassign

import com.wodox.domain.base.BaseParamsUnsafeUseCase
import com.wodox.domain.home.model.local.TaskAssignee
import com.wodox.domain.home.repository.TaskAssignRepository
import java.util.UUID
import javax.inject.Inject

class GetTaskAssignByTaskId @Inject constructor(
    private val userRepository: TaskAssignRepository
) : BaseParamsUnsafeUseCase<UUID, TaskAssignee?>() {
    override suspend fun execute(params: UUID): TaskAssignee? {
        return userRepository.getTaskAssignByTaskID(params)
    }
}