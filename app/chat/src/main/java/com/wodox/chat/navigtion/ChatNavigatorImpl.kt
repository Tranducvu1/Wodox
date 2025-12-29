package com.wodox.chat.navigtion

import android.content.Context
import com.wodox.chat.model.Constant
import com.wodox.chat.ui.channelchat.ChannelChatActivity
import com.wodox.chat.ui.channel.ChannelListActivity
import com.wodox.chat.ui.message.MessageActivity
import com.wodox.common.navigation.ChatNavigator
import com.wodox.core.extension.openActivity
import com.wodox.domain.chat.model.UserWithFriendStatus
import java.util.UUID

class ChatNavigatorImpl : ChatNavigator {

    override fun openMessage(
        context: Context,
        userWithFriendStatus: UserWithFriendStatus
    ) {
        return context.openActivity<MessageActivity>(
            Constant.Intents.USER_ID to userWithFriendStatus
        )
    }

    override fun openChannelList(context: Context) {
        return context.openActivity<ChannelListActivity>()
    }

    override fun openChannelChat(context: Context, channelId: UUID) {
        return context.openActivity<ChannelChatActivity>(
            Constant.Intents.CHANNEL_ID to channelId.toString()
        )
    }
}