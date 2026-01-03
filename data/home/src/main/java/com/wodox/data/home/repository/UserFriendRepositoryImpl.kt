package com.wodox.data.home.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.wodox.data.home.datasource.local.database.task.entity.UserFriendEntity
import com.wodox.data.home.datasource.local.database.task.mapper.UserFriendMapper
import com.wodox.domain.home.model.local.FriendStatus
import com.wodox.domain.home.model.local.UserFriend
import com.wodox.domain.home.repository.UserFriendRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.*
import javax.inject.Inject

class UserFriendRepositoryImpl @Inject constructor(
    private val mapper: UserFriendMapper
) : UserFriendRepository {

    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    private val collection = firestore.collection("user_friends")

    override fun getAll(): Flow<List<UserFriend>> = callbackFlow {
        val listener = collection
            .whereEqualTo("deletedAt", null)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val list = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(UserFriendEntity::class.java)?.let { entity ->
                        mapper.mapToDomain(entity)
                    }
                } ?: emptyList()
                trySend(list)
            }
        awaitClose { listener.remove() }
    }

    override fun getFriendSent(userId: UUID): Flow<List<UserFriend>> = callbackFlow {
        val listener = collection
            .whereEqualTo("userId", userId.toString())
            .whereEqualTo("deletedAt", null)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val list = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(UserFriendEntity::class.java)?.let { entity ->
                        mapper.mapToDomain(entity)
                    }
                } ?: emptyList()
                trySend(list)
            }
        awaitClose { listener.remove() }
    }

    override fun getFriendRequests(userId: UUID): Flow<List<UserFriend>> = callbackFlow {
        val listener = collection
            .whereEqualTo("friendId", userId.toString())
            .whereEqualTo("status", FriendStatus.PENDING.name)
            .whereEqualTo("deletedAt", null)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val list = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(UserFriendEntity::class.java)?.let { entity ->
                        mapper.mapToDomain(entity)
                    }
                } ?: emptyList()
                trySend(list)
            }
        awaitClose { listener.remove() }
    }

    override fun getAcceptedFriends(userId: UUID): Flow<List<UserFriend>> = callbackFlow {
        val listener = collection
            .whereEqualTo("status", FriendStatus.ACCEPTED.name)
            .whereEqualTo("deletedAt", null)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val list = snapshot?.documents?.mapNotNull { doc ->
                    val entity =
                        doc.toObject(UserFriendEntity::class.java) ?: return@mapNotNull null
                    val userIdStr = userId.toString()
                    if (entity.userId == userIdStr || entity.friendId == userIdStr) {
                        mapper.mapToDomain(entity)
                    } else null
                } ?: emptyList()
                trySend(list)
            }
        awaitClose { listener.remove() }
    }

    override suspend fun getById(relationId: UUID): UserFriend? {
        return try {
            val doc = collection.document(relationId.toString()).get().await()
            doc.toObject(UserFriendEntity::class.java)?.let { mapper.mapToDomain(it) }
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun save(userFriend: UserFriend): UserFriend {
        return try {
            Log.d("UserFriendRepo", "Starting save operation for userFriend: ${userFriend.id}")

            val entity = mapper.mapToEntity(userFriend).apply {
                updatedAt = Date()
            }

            Log.d(
                "UserFriendRepo",
                "Mapped entity - ID: ${entity.id}, userId: ${entity.userId}, friendId: ${entity.friendId}, status: ${entity.status}"
            )

            collection.document(entity.id).set(entity).await()

            Log.d("UserFriendRepo", "Save successful for ID: ${entity.id}")

            mapper.mapToDomain(entity)
        } catch (e: Exception) {
            Log.e("UserFriendRepo", "Save failed with error: ${e.message}", e)
            Log.e("UserFriendRepo", "UserFriend data: $userFriend")
            throw e
        }
    }

    override suspend fun deleteSoft(relationId: UUID): Int {
        return try {
            collection.document(relationId.toString())
                .update(
                    mapOf(
                        "deletedAt" to Date(),
                        "updatedAt" to Date()
                    )
                ).await()
            1
        } catch (e: Exception) {
            0
        }
    }

    override suspend fun updateStatus(relationId: UUID, status: FriendStatus): Int {
        return try {
            collection.document(relationId.toString())
                .update(
                    mapOf(
                        "status" to status.name,
                        "updatedAt" to Date()
                    )
                ).await()
            1
        } catch (e: Exception) {
            0
        }
    }

    override suspend fun findRelation(userId: UUID, friendId: UUID): UserFriend? {
        return try {
            Log.d("FindRelation", "Finding relation BOTH directions")

            val snapshot = collection
                .whereIn("userId", listOf(userId.toString(), friendId.toString()))
                .whereIn("friendId", listOf(userId.toString(), friendId.toString()))
                .whereEqualTo("deletedAt", null)
                .get()
                .await()

            snapshot.documents
                .mapNotNull { it.toObject(UserFriendEntity::class.java) }
                .firstOrNull {
                    (it.userId == userId.toString() && it.friendId == friendId.toString()) ||
                            (it.userId == friendId.toString() && it.friendId == userId.toString())
                }
                ?.let { mapper.mapToDomain(it) }

        } catch (e: Exception) {
            Log.e("FindRelation", "Error finding relation", e)
            null
        }
    }
}