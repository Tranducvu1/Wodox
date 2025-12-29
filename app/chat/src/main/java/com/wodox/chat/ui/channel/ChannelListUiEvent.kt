package com.wodox.chat.ui.channel

import com.wodox.domain.chat.model.Channel
import java.util.UUID

sealed class ChannelListUiEvent {
    data class ChannelCreated(val channel: Channel) : ChannelListUiEvent()
    data class Error(val message: String) : ChannelListUiEvent()
    data class ChannelJoined(val channelId: UUID) : ChannelListUiEvent()
    data class ChannelLeft(val channelId: UUID) : ChannelListUiEvent()
}