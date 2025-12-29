package com.wodox.chat.ui.channel

import java.util.UUID


sealed class ChannelListUiAction {
    object LoadAllChannels : ChannelListUiAction()
    object LoadJoinedChannels : ChannelListUiAction()
    object LoadMyChannels : ChannelListUiAction()
    data class CreateChannel(
        val name: String,
        val description: String?,
        val isPrivate: Boolean
    ) : ChannelListUiAction()
    data class SearchChannels(val query: String) : ChannelListUiAction()
    data class DeleteChannel(val channelId: java.util.UUID) : ChannelListUiAction()
    data class JoinChannel(val channelId: UUID) : ChannelListUiAction()
    data class LeaveChannel(val channelId: UUID) : ChannelListUiAction()
}