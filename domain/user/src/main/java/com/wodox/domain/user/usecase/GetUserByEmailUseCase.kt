package com.wodox.domain.user.usecase

import com.wodox.domain.base.BaseParamsUnsafeUseCase
import com.wodox.domain.user.model.User
import com.wodox.domain.user.repository.UserRepository
import javax.inject.Inject


class GetUserByEmailUseCase @Inject constructor(
    private val userRepository: UserRepository
) : BaseParamsUnsafeUseCase<String, User?>() {
    override suspend fun execute(params: String): User? {
        return userRepository.getUserByEmail(params)
    }
}
