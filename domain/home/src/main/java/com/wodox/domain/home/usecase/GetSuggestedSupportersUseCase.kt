package com.wodox.domain.home.usecase


import com.wodox.domain.base.BaseParamsUnsafeUseCase
import com.wodox.domain.home.model.local.Difficulty
import com.wodox.domain.home.model.local.Priority
import com.wodox.domain.home.repository.TaskRepository
import com.wodox.domain.user.model.User
import java.util.UUID
import javax.inject.Inject

data class SuggestedSupportersParams(
    val taskDifficulty: Difficulty, val taskPriority: Priority, val currentUserId: UUID
)

class GetSuggestedSupportersUseCase @Inject constructor(
    private val repository: TaskRepository
) : BaseParamsUnsafeUseCase<SuggestedSupportersParams, List<User>>() {
    override suspend fun execute(params: SuggestedSupportersParams): List<User> {
        return repository.getSuggestedSupporters(
            taskDifficulty = params.taskDifficulty,
            taskPriority = params.taskPriority,
            currentUserId = params.currentUserId
        )
    }
}