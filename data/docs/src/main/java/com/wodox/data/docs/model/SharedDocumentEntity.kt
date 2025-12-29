package com.wodox.data.docs.model

import com.wodox.domain.docs.model.model.InvitedUser

data class SharedDocumentEntity(
    val documentId: String = "",
    val documentTitle: String = "",
    val ownerUserId: String = "",
    val ownerUserName: String = "",
    val ownerUserEmail: String = "",
    val invitedUsers: List<InvitedUser> = emptyList(),
    val invitedUserIds: List<String> = emptyList(),
    val htmlContent: String = "",
    val sharedAt: Long = 0L,
    val lastModified: Long = 0L
)
