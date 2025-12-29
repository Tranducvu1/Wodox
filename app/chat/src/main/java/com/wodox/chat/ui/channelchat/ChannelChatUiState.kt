package com.wodox.chat.ui.channelchat

import com.wodox.domain.chat.model.Channel
import com.wodox.domain.chat.model.ChannelMember
import com.wodox.domain.chat.model.ChannelMessage
import com.wodox.domain.user.model.User


data class ChannelChatUiState(
    val isLoading: Boolean = false,
    val channel: Channel? = null,
    val messages: List<ChannelMessage> = emptyList(),
    val error: String? = null,
    val currentUser: User? = null,
    val members: List<ChannelMember> = emptyList(),
)
