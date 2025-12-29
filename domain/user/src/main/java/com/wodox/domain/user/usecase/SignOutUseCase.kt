package com.wodox.domain.user.usecase

import com.wodox.domain.base.BaseNoParamsUnsafeUseCase
import com.wodox.domain.user.repository.UserRepository
import javax.inject.Inject


class SignOutUseCase @Inject constructor(
    private val userRepository: UserRepository
) : BaseNoParamsUnsafeUseCase<Unit>() {
    override suspend fun execute() {
        return userRepository.logout()
    }
}