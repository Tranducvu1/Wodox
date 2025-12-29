package com.wodox.chat.ui.chat

sealed class ChatUiEvent {
    object ResetNotificationAnimation : ChatUiEvent()

    object NavigatorTask : ChatUiEvent()
}