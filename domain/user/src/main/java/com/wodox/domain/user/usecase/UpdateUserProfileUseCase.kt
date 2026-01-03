package com.wodox.domain.user.usecase

import com.wodox.domain.base.BaseParamsUnsafeUseCase
import com.wodox.domain.user.model.User
import com.wodox.domain.user.repository.UserRepository
import javax.inject.Inject

data class UpdateUserProfileParams(
    val fullName: String,
    val email: String,
    val phone :String,
    val bio :String
)

class UpdateUserProfileUseCase @Inject constructor(
    private val repository: UserRepository,
) : BaseParamsUnsafeUseCase<UpdateUserProfileParams, User?>() {
    override suspend fun execute(params: UpdateUserProfileParams): User? {
        return try {
            val currentUser = repository.getCurrentUser()
                ?: return null
            val updatedUser = currentUser.copy(
                name = params.fullName,
                email = params.email,
                phone = params.phone,
                bio = params.bio
            )
            repository.saveUserToFirebase(updatedUser)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
