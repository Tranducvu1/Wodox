package com.wodox.data.docs.repository

import com.wodox.base.base.AbstractMapper
import com.wodox.data.docs.model.SharedDocumentEntity
import com.wodox.domain.docs.model.model.DocumentPermission
import com.wodox.domain.docs.model.model.InvitedUser
import com.wodox.domain.docs.model.model.SharedDocument
import java.util.UUID
import javax.inject.Inject

class SharedDocumentMapper @Inject constructor() :
    AbstractMapper<SharedDocumentEntity, SharedDocument>() {

    override fun mapToDomain(entity: SharedDocumentEntity): SharedDocument {
        return SharedDocument(
            documentId = entity.documentId,
            documentTitle = entity.documentTitle,
            ownerUserId = entity.ownerUserId,
            ownerUserName = entity.ownerUserName,
            ownerUserEmail = entity.ownerUserEmail,
            invitedUsers = entity.invitedUsers.mapNotNull { userMap ->
                mapInvitedUserToDomain(userMap)
            },
            htmlContent = entity.htmlContent,
            sharedAt = entity.sharedAt,
            lastModified = entity.lastModified
        )
    }

    override fun mapToEntity(domain: SharedDocument): SharedDocumentEntity {
        return SharedDocumentEntity(
            documentId = domain.documentId,
            documentTitle = domain.documentTitle,
            ownerUserId = domain.ownerUserId,
            ownerUserName = domain.ownerUserName,
            ownerUserEmail = domain.ownerUserEmail,
            invitedUsers = domain.invitedUsers.map { user ->
                mapInvitedUserToEntity(user)
            },
            invitedUserIds = domain.invitedUsers.map { it.userId.toString() },
            htmlContent = domain.htmlContent,
            sharedAt = domain.sharedAt,
            lastModified = domain.lastModified
        )
    }

    private fun mapInvitedUserToDomain(userMap: Map<String, Any>): InvitedUser? {
        return try {
            InvitedUser(
                userId = UUID.fromString(userMap["userId"] as? String ?: return null),
                userName = userMap["userName"] as? String ?: "",
                userEmail = userMap["userEmail"] as? String ?: "",
                permission = try {
                    DocumentPermission.valueOf(userMap["permission"] as? String ?: "VIEW")
                } catch (e: Exception) {
                    DocumentPermission.VIEW
                },
                invitedAt = when (val invitedAt = userMap["invitedAt"]) {
                    is Long -> invitedAt
                    is Int -> invitedAt.toLong()
                    else -> System.currentTimeMillis()
                }
            )
        } catch (e: Exception) {
            android.util.Log.e("SharedDocumentMapper", "Error mapping invited user from Map", e)
            null
        }
    }

    private fun mapInvitedUserToEntity(user: InvitedUser): Map<String, Any> {
        return mapOf(
            "userId" to user.userId.toString(),
            "userName" to user.userName,
            "userEmail" to user.userEmail,
            "permission" to user.permission.name,
            "invitedAt" to user.invitedAt
        )
    }
}