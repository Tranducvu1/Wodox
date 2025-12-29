package com.wodox.chat.ui.channel

import com.wodox.domain.chat.model.Channel
import com.wodox.domain.user.model.User

data class ChannelListUiState(
    val isLoading: Boolean = false,
    val channels: List<Channel> = emptyList(),
    val error: String? = null,
    val searchQuery: String = "",
    val currentUser: User? = null,
    val channelsJoin: List<Channel> = emptyList(),

    )