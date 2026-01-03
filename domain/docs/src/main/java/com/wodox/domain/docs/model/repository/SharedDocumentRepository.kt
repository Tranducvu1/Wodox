package com.wodox.domain.docs.model.repository

import com.wodox.domain.docs.model.model.SharedDocument

interface SharedDocumentRepository {
    suspend fun saveSharedDocument(document: SharedDocument)
    suspend fun getSharedDocumentById(docId: String): SharedDocument?
    suspend fun getSharedDocumentsForUser(firebaseUid: String): List<SharedDocument>
    suspend fun deleteSharedDocument(docId: String)
    suspend fun updateSharedDocument(document: SharedDocument)

    suspend fun getDocumentsByUserId(userId: String): List<SharedDocument>

}