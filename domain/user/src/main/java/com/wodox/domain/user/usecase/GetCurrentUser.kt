package com.wodox.domain.user.usecase

import com.wodox.domain.base.BaseNoParamsUnsafeUseCase
import com.wodox.domain.user.model.User
import com.wodox.domain.user.repository.UserRepository
import javax.inject.Inject

class GetCurrentUser @Inject constructor(
    private val userRepository: UserRepository
) : BaseNoParamsUnsafeUseCase<User?>() {
    override suspend fun execute(): User? {
        return userRepository.getCurrentUser()
    }
}