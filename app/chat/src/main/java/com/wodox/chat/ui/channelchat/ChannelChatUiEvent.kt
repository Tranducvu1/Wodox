package com.wodox.chat.ui.channelchat

sealed class ChannelChatUiEvent {
    object MessageSent : ChannelChatUiEvent()
    data class Error(val message: String) : ChannelChatUiEvent()
}
