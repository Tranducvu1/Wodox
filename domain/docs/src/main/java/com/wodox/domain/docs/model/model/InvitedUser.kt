package com.wodox.domain.docs.model.model

import java.util.UUID

data class InvitedUser(
    val userId:  UUID = UUID.randomUUID(),
    val userName: String = "",
    val userEmail: String = "",
    val permission: DocumentPermission = DocumentPermission.VIEW,
    val invitedAt: Long = System.currentTimeMillis()
)