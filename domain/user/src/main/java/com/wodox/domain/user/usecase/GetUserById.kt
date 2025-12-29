package com.wodox.domain.user.usecase

import com.wodox.domain.base.BaseParamsUnsafeUseCase
import com.wodox.domain.user.model.User
import com.wodox.domain.user.repository.UserRepository
import java.util.UUID
import javax.inject.Inject



class GetUserById @Inject constructor(
    private val userRepository: UserRepository
) : BaseParamsUnsafeUseCase<UUID, User?>() {
    override suspend fun execute(params: UUID): User? {
        return userRepository.getUserById(params)
    }
}