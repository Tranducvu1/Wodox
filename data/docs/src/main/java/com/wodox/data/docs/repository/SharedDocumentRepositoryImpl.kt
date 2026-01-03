package com.wodox.data.docs.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.wodox.data.docs.model.SharedDocumentEntity
import com.wodox.domain.docs.model.model.SharedDocument
import com.wodox.domain.docs.model.repository.SharedDocumentRepository
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class SharedDocumentRepositoryImpl @Inject constructor(
    private val mapper: SharedDocumentMapper
) : SharedDocumentRepository {
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val collection = firestore.collection("shared_documents")

    override suspend fun saveSharedDocument(document: SharedDocument) {
        val entity = mapper.mapToEntity(document)
        collection.document(document.documentId)
            .set(entity)
            .await()
    }

    override suspend fun getSharedDocumentById(docId: String): SharedDocument? {
        return try {
            val snapshot = collection.document(docId).get().await()
            snapshot.toObject(SharedDocumentEntity::class.java)
                ?.let { mapper.mapToDomain(it) }
        } catch (e: Exception) {
            android.util.Log.e("SharedDocRepository", "Error getting document by id", e)
            e.printStackTrace()
            null
        }
    }

    override suspend fun getSharedDocumentsForUser(firebaseUid: String): List<SharedDocument> {
        return try {
            val snapshot = collection
                .whereArrayContains("invitedUserIds", firebaseUid)
                .get()
                .await()

            snapshot.documents.mapNotNull {
                try {
                    it.toObject(SharedDocumentEntity::class.java)
                        ?.let(mapper::mapToDomain)
                } catch (e: Exception) {
                    android.util.Log.e("SharedDocRepository", "Error mapping shared doc", e)
                    null
                }
            }
        } catch (e: Exception) {
            android.util.Log.e("SharedDocRepository", "Error getting shared documents", e)
            e.printStackTrace()
            emptyList()
        }
    }

    override suspend fun deleteSharedDocument(docId: String) {
        try {
            collection.document(docId).delete().await()
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }

    override suspend fun updateSharedDocument(document: SharedDocument) {
        try {
            val entity = mapper.mapToEntity(document)
            collection.document(document.documentId)
                .set(entity)
                .await()
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }

    override suspend fun getDocumentsByUserId(userId: String): List<SharedDocument> {
        return try {
            android.util.Log.d("SharedDocRepository", "=== START getDocumentsByUserId ===")
            android.util.Log.d("SharedDocRepository", "Query userId: $userId")

            val snapshot = collection
                .whereEqualTo("ownerUserId", userId)
                .orderBy("lastModified", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .get()
                .await()

            android.util.Log.d("SharedDocRepository", "Documents found: ${snapshot.documents.size}")

            val documents = snapshot.documents.mapNotNull { doc ->
                try {
                    val entity = doc.toObject(SharedDocumentEntity::class.java)
                    entity?.let {
                        val domain = mapper.mapToDomain(it)
                        android.util.Log.d("SharedDocRepository", "Mapped: ${domain.documentId} - ${domain.documentTitle}")
                        domain
                    }
                } catch (e: Exception) {
                    android.util.Log.e("SharedDocRepository", "Error mapping document ${doc.id}", e)
                    null
                }
            }

            android.util.Log.d("SharedDocRepository", "Total documents: ${documents.size}")
            android.util.Log.d("SharedDocRepository", "=== END getDocumentsByUserId ===")

            documents

        } catch (e: Exception) {
            android.util.Log.e("SharedDocRepository", "=== ERROR getDocumentsByUserId ===", e)
            e.printStackTrace()
            emptyList()
        }
    }
}