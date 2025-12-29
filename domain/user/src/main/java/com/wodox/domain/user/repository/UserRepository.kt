package com.wodox.domain.user.repository

import com.wodox.domain.user.model.User
import kotlinx.coroutines.flow.Flow
import java.util.UUID

interface UserRepository {
    suspend fun saveUserToFirebase(user: User): User?

    fun getAllUserFromFirebase(): Flow<List<User>>

    suspend fun getUserByEmail(email: String): User?

    suspend fun getUserById(id: UUID): User?

    suspend fun getAllFriends(userId: UUID): List<User>

    suspend fun getCurrentUserUUID(): UUID?

    suspend fun getCurrentUserEmail(): String?

    suspend fun logout()


    suspend fun getCurrentUser(): User?

}