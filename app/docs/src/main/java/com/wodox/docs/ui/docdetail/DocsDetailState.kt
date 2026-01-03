package com.wodox.docs.ui.docdetail

import com.wodox.docs.model.EventColorItem
import com.wodox.domain.docs.model.model.InvitedUser
import com.wodox.domain.docs.model.model.SharedDocument
import com.wodox.domain.user.model.User

data class DocsDetailState(
    val colors: ArrayList<EventColorItem> = arrayListOf(),
    val textSize: Int = 16,
    val userInvite: User? = null,
    val invitedUsers: List<InvitedUser> = emptyList(),
    val documentId: String = "",
    val currentUser: User? = null,
    val sharedDocument: SharedDocument? = null
)
