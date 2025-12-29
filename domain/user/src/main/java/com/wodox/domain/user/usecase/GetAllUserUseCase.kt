package com.wodox.domain.user.usecase

import com.wodox.domain.base.BaseNoParamsFlowUnsafeUseCase
import com.wodox.domain.user.model.User
import com.wodox.domain.user.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllUserUseCase @Inject constructor(
    private val userRepository: UserRepository
) : BaseNoParamsFlowUnsafeUseCase<List<User>>() {
    override suspend fun execute(): Flow<List<User>> {
        return userRepository.getAllUserFromFirebase()
    }
}
