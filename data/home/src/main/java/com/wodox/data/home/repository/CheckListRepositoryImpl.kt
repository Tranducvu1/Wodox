package com.wodox.data.home.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.wodox.data.home.datasource.local.database.task.entity.CheckListEntity
import com.wodox.data.home.datasource.local.database.task.mapper.CheckListMapper
import com.wodox.domain.home.model.local.CheckList
import com.wodox.domain.home.repository.CheckListRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.Date
import javax.inject.Inject

class CheckListRepositoryImpl @Inject constructor(
    private val mapper: CheckListMapper
) : CheckListRepository {
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val collection = firestore.collection("checklists")

    override fun getAllCheckListByTaskID(id: String): Flow<List<CheckList>> = callbackFlow {
        val listener = collection
            .whereEqualTo("taskId", id)
            .whereEqualTo("deletedAt", null)
            .orderBy("createdAt", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val list = snapshot?.documents?.mapNotNull { doc ->
                    try {
                        val entity = doc.toObject(CheckListEntity::class.java)
                        entity?.let { mapper.mapToDomain(it) }
                    } catch (e: Exception) {
                        android.util.Log.e("CheckListRepo", "Error mapping document", e)
                        null
                    }
                } ?: emptyList()
                trySend(list)
            }
        awaitClose { listener.remove() }
    }

    override suspend fun save(checkList: CheckList): CheckList? {
        return try {
            val entity = mapper.mapToEntity(checkList).apply {
                this.updatedAt = Date()
            }
            collection.document(entity.id).set(entity).await()
            mapper.mapToDomain(entity)
        } catch (e: Exception) {
            android.util.Log.e("CheckListRepo", "Error saving checklist", e)
            null
        }
    }
}