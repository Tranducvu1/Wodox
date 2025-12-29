package com.wodox.domain.docs.model.model

import java.util.UUID

data class InvitedUser(
    val userId: UUID,
    val userName: String,
    val userEmail: String,
    val permission: DocumentPermission,
    val invitedAt: Long = System.currentTimeMillis()
)