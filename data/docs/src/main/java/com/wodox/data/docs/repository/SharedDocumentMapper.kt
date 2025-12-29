package com.wodox.data.docs.repository

import com.wodox.base.base.AbstractMapper
import com.wodox.data.docs.model.SharedDocumentEntity
import com.wodox.domain.docs.model.model.SharedDocument
import java.util.UUID
import javax.inject.Inject

class SharedDocumentMapper @Inject constructor() :
    AbstractMapper<SharedDocumentEntity, SharedDocument>() {
    override fun mapToDomain(entity: SharedDocumentEntity): SharedDocument {
        return SharedDocument(
            documentId = entity.documentId,
            documentTitle = entity.documentTitle,
            ownerUserId = UUID.fromString(entity.ownerUserId),
            ownerUserName = entity.ownerUserName,
            ownerUserEmail = entity.ownerUserEmail,
            invitedUsers = entity.invitedUsers,
            htmlContent = entity.htmlContent,
            sharedAt = entity.sharedAt,
            lastModified = entity.lastModified
        )
    }

    override fun mapToEntity(domain: SharedDocument): SharedDocumentEntity {
        return SharedDocumentEntity(
            documentId = domain.documentId,
            documentTitle = domain.documentTitle,
            ownerUserId = domain.ownerUserId.toString(),
            ownerUserName = domain.ownerUserName,
            ownerUserEmail = domain.ownerUserEmail,
            invitedUsers = domain.invitedUsers,
            invitedUserIds = domain.invitedUsers.map { it.userId.toString() },
            htmlContent = domain.htmlContent,
            sharedAt = domain.sharedAt,
            lastModified = domain.lastModified
        )
    }
}
