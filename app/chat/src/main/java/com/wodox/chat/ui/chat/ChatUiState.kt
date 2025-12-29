package com.wodox.chat.ui.chat

import com.wodox.domain.chat.model.Channel
import com.wodox.domain.chat.model.UserWithFriendStatus
import com.wodox.domain.chat.model.local.ActivityItem
import com.wodox.domain.chat.model.local.Notification
import com.wodox.domain.home.model.local.Task
import java.util.UUID

data class ChatUiState (
    val listActivityItem : List<ActivityItem> = emptyList(),
    val listUser : List<UserWithFriendStatus> = emptyList(),
    val listNotifications: ArrayList<Notification> = arrayListOf(),
    val email: String? = null,
    val userId: UUID? = null,
    val unreadNotificationCount: Int = 0,
    val hasNewNotification: Boolean = false,
    val task: Task?=null,
    val channels: List<Channel> = emptyList(),
)