package com.wodox.data.user.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.wodox.data.user.database.entity.UserEntity
import com.wodox.data.user.database.mapper.UserMapper
import com.wodox.domain.user.model.User
import com.wodox.domain.user.repository.UserRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.*
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val mapper: UserMapper
) : UserRepository {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val userCollection = firestore.collection("users")

    override suspend fun saveUserToFirebase(user: User): User? {
        return try {
            val currentUser = auth.currentUser ?: return null
            val uid = currentUser.uid

            val userEntity = mapper.mapToEntity(
                user.copy(
                    id = UUID.nameUUIDFromBytes(uid.toByteArray()),
                    isActive = true,
                    updatedAt = Date()
                )
            )

            userCollection.document(uid).set(userEntity).await()

            user
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }


    override fun getAllUserFromFirebase(): Flow<List<User>> = callbackFlow {
        val listener = userCollection
            .addSnapshotListener { snapshots, error ->
                if (error != null || snapshots == null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }

                val users = snapshots.documents.mapNotNull { doc ->
                    doc.toObject(UserEntity::class.java)?.let {
                        mapper.mapToDomain(it)
                    }
                }
                trySend(users)
            }

        awaitClose { listener.remove() }
    }

    override suspend fun getUserByEmail(email: String): User? {
        return try {
            val snapshot = userCollection
                .whereEqualTo("email", email)
                .limit(1)
                .get()
                .await()
            val entity = snapshot.documents.firstOrNull()?.toObject(UserEntity::class.java)
            entity?.let {
                mapper.mapToDomain(it)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override suspend fun getUserById(id: UUID): User? {
        return try {
            val snapshot = userCollection
                .whereEqualTo("id", id.toString())
                .limit(1)
                .get()
                .await()

            val entity = snapshot.documents.firstOrNull()?.toObject(UserEntity::class.java)

            if (entity == null) {
                Log.w("UserRepository", "No user found for id: $id")
                return null
            }

            mapper.mapToDomain(entity)
        } catch (e: Exception) {
            Log.e("UserRepository", "Error retrieving user by id: $id", e)
            null
        }
    }

    override suspend fun getAllFriends(userId: UUID): List<User> {
        TODO("Not yet implemented")
    }


    override suspend fun getCurrentUserEmail(): String? {
        val currentUid = auth.currentUser?.uid ?: return null
        return try {
            val snapshot = userCollection.document(currentUid).get().await()
            val entity = snapshot.toObject(UserEntity::class.java)
            entity?.let { mapper.mapToDomain(it).email }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override suspend fun logout() {
        try {
            auth.signOut()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override suspend fun getCurrentUser(): User? {
        val currentUid = auth.currentUser?.uid ?: return null

        return try {
            val snapshot = userCollection
                .document(currentUid)
                .get()
                .await()

            val entity = snapshot.toObject(UserEntity::class.java)

            entity?.let {
                mapper.mapToDomain(it)
            }
        } catch (e: Exception) {
            Log.e("UserRepository", "Error getting current user", e)
            null
        }
    }


    override suspend fun getCurrentUserUUID(): UUID? {
        val currentUid = auth.currentUser?.uid ?: return null
        return try {
            val snapshot = userCollection.document(currentUid).get().await()
            val entity = snapshot.toObject(UserEntity::class.java)
            entity?.let { mapper.mapToDomain(it).id }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

}
