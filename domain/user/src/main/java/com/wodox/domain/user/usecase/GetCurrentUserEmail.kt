package com.wodox.domain.user.usecase

import com.wodox.domain.base.BaseNoParamsUnsafeUseCase
import com.wodox.domain.user.repository.UserRepository
import javax.inject.Inject

class GetCurrentUserEmail @Inject constructor(
    private val userRepository: UserRepository
) : BaseNoParamsUnsafeUseCase<String?>() {
    override suspend fun execute(): String? {
        return userRepository.getCurrentUserEmail()
    }
}