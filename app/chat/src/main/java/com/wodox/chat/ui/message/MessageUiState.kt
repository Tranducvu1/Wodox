package com.wodox.chat.ui.message

import com.wodox.domain.chat.model.UserWithFriendStatus
import com.wodox.domain.chat.model.local.MessageChat
import com.wodox.domain.user.model.User

data class MessageUiState(
    val messages: List<MessageChat> = emptyList(),
    val messageFriends: List<MessageChat> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val searchQuery: String = "",
    val selectedMessage: MessageChat? = null,
    val listUser : UserWithFriendStatus? = null,
    val friend : User? = null
    )