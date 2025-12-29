package com.wodox.domain.home.usecase.taskassign

import com.wodox.domain.base.BaseParamsUnsafeUseCase
import com.wodox.domain.home.model.local.TaskAssignee
import com.wodox.domain.home.repository.TaskAssignRepository
import java.util.UUID
import javax.inject.Inject

class GetTaskAssignByUserIdUseCase @Inject constructor(
    private val userRepository: TaskAssignRepository
) : BaseParamsUnsafeUseCase<UUID, List<TaskAssignee>?>() {
    override suspend fun execute(params: UUID): List<TaskAssignee>? {
        return userRepository.getTaskAssignByUserID(params)
    }
}