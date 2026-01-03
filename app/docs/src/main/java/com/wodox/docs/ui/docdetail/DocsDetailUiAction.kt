package com.wodox.docs.ui.docdetail

import com.wodox.domain.docs.model.model.InvitedUser
import java.util.UUID

sealed class DocsDetailUiAction {
    data class UpdateTextSize(val size: Int) : DocsDetailUiAction()
    data class AssignUser(val id: UUID) : DocsDetailUiAction()
    data class AddInvitedUser(val user: InvitedUser) : DocsDetailUiAction()
    data class RemoveInvitedUser(val userId: UUID) : DocsDetailUiAction()
    data class LoadInvitedUsers(val invitedUsers: List<InvitedUser>) : DocsDetailUiAction()
    data class SetDocumentId(val docId: String) : DocsDetailUiAction()
}