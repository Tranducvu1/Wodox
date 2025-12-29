package com.wodox.ui.task.userbottomsheet

import com.wodox.domain.chat.model.UserWithFriendStatus

data class ListUserUiState(
    val listUser : List<UserWithFriendStatus> = emptyList(),
    )