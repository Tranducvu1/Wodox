package com.wodox.domain.user.usecase

import com.wodox.domain.base.BaseParamsUnsafeUseCase
import com.wodox.domain.user.model.User
import com.wodox.domain.user.repository.UserRepository
import javax.inject.Inject


class SaveUserUseCase @Inject constructor(
    private val repository: UserRepository
) : BaseParamsUnsafeUseCase<User, User?>() {
    override suspend fun execute(params: User): User? {
        return repository.saveUserToFirebase(params)
    }
}
