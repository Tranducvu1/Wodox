package com.wodox.docs.ui


import com.wodox.domain.docs.model.model.InvitedUser
import java.util.UUID

sealed class DocUiAction {
    data class UpdateTextSize(val size: Int) : DocUiAction()
    data class AssignUser(val id: UUID) : DocUiAction()
    data class AddInvitedUser(val user: InvitedUser) : DocUiAction()
    data class RemoveInvitedUser(val userId: UUID) : DocUiAction()
    data class LoadInvitedUsers(val invitedUsers: List<InvitedUser>) : DocUiAction()
    data class SetDocumentId(val docId: String) : DocUiAction()
}