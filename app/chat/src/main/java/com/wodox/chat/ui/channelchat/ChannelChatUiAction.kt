package com.wodox.chat.ui.channelchat

sealed class ChannelChatUiAction {
    data class SendMessage(val text: String) : ChannelChatUiAction()
    object LoadMessages : ChannelChatUiAction()
    object ClearUnread : ChannelChatUiAction()
}