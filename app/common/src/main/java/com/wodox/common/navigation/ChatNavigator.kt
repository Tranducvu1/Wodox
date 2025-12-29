package com.wodox.common.navigation

import android.content.Context
import com.wodox.domain.chat.model.UserWithFriendStatus
import java.util.UUID

interface ChatNavigator {
    fun openMessage(context: Context, userWithFriendStatus: UserWithFriendStatus)
    fun openChannelList(context: Context)
    fun openChannelChat(context: Context, channelId: UUID)
}