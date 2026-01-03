package com.wodox.domain.docs.model.model

import java.util.UUID

data class SharedDocument(
    val documentId: String,
    val documentTitle: String,
    val ownerUserId: String,
    val ownerUserName: String,
    val ownerUserEmail: String,
    val invitedUsers: List<InvitedUser> = emptyList(),
    val htmlContent: String = "",
    val sharedAt: Long = System.currentTimeMillis(),
    val lastModified: Long = System.currentTimeMillis()
)
