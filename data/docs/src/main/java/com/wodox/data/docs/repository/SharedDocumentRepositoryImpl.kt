package com.wodox.data.docs.repository

import android.content.Context
import com.google.firebase.firestore.FirebaseFirestore
import com.wodox.data.docs.model.SharedDocumentEntity
import com.wodox.domain.docs.model.model.SharedDocument
import com.wodox.domain.docs.model.repository.SharedDocumentRepository
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject

class SharedDocumentRepositoryImpl @Inject constructor(
    private val context: Context,
    private val mapper: SharedDocumentMapper
) : SharedDocumentRepository {
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val collection = firestore.collection("shared_documents")
    private val sharedPref = context.getSharedPreferences("shared_docs", Context.MODE_PRIVATE)

    override suspend fun saveSharedDocument(document: SharedDocument) {
        val entity = mapper.mapToEntity(document)

        collection.document(document.documentId)
            .set(entity)
            .await()

        saveToLocal(document)
    }

    override suspend fun getSharedDocumentById(docId: String): SharedDocument? {
        return try {
            val snapshot = collection.document(docId).get().await()
            snapshot.toObject(SharedDocumentEntity::class.java)
                ?.let { mapper.mapToDomain(it) }
        } catch (e: Exception) {
            getFromLocal(docId)
        }
    }


    override suspend fun getSharedDocumentsForUser(
        firebaseUid: String
    ): List<SharedDocument> {
        return try {
            val snapshot = collection
                .whereArrayContains("invitedUserIds", firebaseUid)
                .get()
                .await()

            snapshot.documents.mapNotNull {
                it.toObject(SharedDocumentEntity::class.java)
                    ?.let(mapper::mapToDomain)
            }

        } catch (e: Exception) {
            emptyList()
        }
    }


    override suspend fun deleteSharedDocument(docId: String) {
        collection.document(docId).delete().await()
        sharedPref.edit().remove("doc_$docId").apply()
    }

    override suspend fun updateSharedDocument(document: SharedDocument) {
        val data = document.toFirestoreMap()
        collection.document(document.documentId)
            .set(data)
            .await()

        saveToLocal(document)
    }


    private fun saveToLocal(document: SharedDocument) {
        sharedPref.edit().apply {
            putString("doc_${document.documentId}_title", document.documentTitle)
            putString("doc_${document.documentId}_owner", document.ownerUserName)
            putString("doc_${document.documentId}_content", document.htmlContent)
            putLong("doc_${document.documentId}_modified", document.lastModified)
            apply()
        }
    }

    private fun getFromLocal(docId: String): SharedDocument? {
        val title = sharedPref.getString("doc_${docId}_title", null) ?: return null
        val owner = sharedPref.getString("doc_${docId}_owner", "") ?: ""
        val ownerIdStr = sharedPref.getString("doc_${docId}_ownerId", null) ?: return null
        val content = sharedPref.getString("doc_${docId}_content", "") ?: ""
        val modified = sharedPref.getLong("doc_${docId}_modified", System.currentTimeMillis())

        return SharedDocument(
            documentId = docId,
            documentTitle = title,
            ownerUserId = UUID.fromString(ownerIdStr),
            ownerUserName = owner,
            ownerUserEmail = "",
            htmlContent = content,
            lastModified = modified
        )
    }

    private fun getAllSharedDocsLocal(): List<SharedDocument> {
        val docs = mutableListOf<SharedDocument>()
        sharedPref.all.keys.filter { it.endsWith("_title") }.forEach { key ->
            val docId = key.removePrefix("doc_").removeSuffix("_title")
            getFromLocal(docId)?.let { docs.add(it) }
        }
        return docs
    }
}

private fun SharedDocument.toFirestoreMap(): Map<String, Any?> {
    return mapOf(
        "documentId" to documentId,
        "documentTitle" to documentTitle,
        "ownerUserId" to ownerUserId.toString(),
        "ownerUserName" to ownerUserName,
        "ownerUserEmail" to ownerUserEmail,
        "invitedUsers" to invitedUsers,
        "invitedUserIds" to invitedUsers.map { it.userId.toString() },
        "htmlContent" to htmlContent,
        "sharedAt" to sharedAt,
        "lastModified" to lastModified
    )
}

