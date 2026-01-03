package com.wodox.data.home.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.wodox.data.home.datasource.local.database.task.entity.AttachmentEntity
import com.wodox.data.home.datasource.local.database.task.mapper.AttachmentMapper
import com.wodox.domain.home.model.local.Attachment
import com.wodox.domain.home.repository.AttachmentRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.Date
import java.util.UUID
import javax.inject.Inject

class AttachmentRepositoryImpl @Inject constructor(
    private val mapper: AttachmentMapper
) : AttachmentRepository {
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    private val collection = firestore.collection("attachments")

    override fun getAllTaskByUserID(taskId: UUID): Flow<List<Attachment>> = callbackFlow {
        val listener = collection
            .whereEqualTo("taskId", taskId.toString())
            .whereEqualTo("deletedAt", null)
            .orderBy("createdAt", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val list = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(AttachmentEntity::class.java)?.let { entity ->
                        mapper.mapToDomain(entity)
                    }
                } ?: emptyList()
                trySend(list)
            }
        awaitClose { listener.remove() }
    }

    override suspend fun save(attachment: Attachment): Attachment? {
        val entity = mapper.mapToEntity(attachment).apply {
            this.updatedAt = Date()
        }
        val firestoreEntity = entity.toFirestoreMap()
        collection.document(entity.id.toString()).set(firestoreEntity).await()
       return  mapper.mapToDomain(entity)
    }

    private fun AttachmentEntity.toFirestoreMap(): Map<String, Any?> {
        return mapOf(
            "id" to id.toString(),
            "taskId" to taskId.toString(),
            "subTaskId" to subTaskId.toString(),
            "name" to name,
            "uri" to uri,
            "type" to type,
            "url" to url,
            "createdAt" to createdAt,
            "updatedAt" to updatedAt,
            "deletedAt" to deletedAt
        )
    }
}