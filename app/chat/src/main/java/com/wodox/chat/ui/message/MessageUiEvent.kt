package com.wodox.chat.ui.message

import com.wodox.domain.chat.model.local.MessageChat
import java.util.UUID

sealed class MessageUiEvent {
    data class MessageUpdated(val messageId: UUID) : MessageUiEvent()

    data class MessageDeleted(val messageId: UUID) : MessageUiEvent()

    data class MessageDetailLoaded(val message: MessageChat) : MessageUiEvent()

    object AllMessagesCleared : MessageUiEvent()

    data class Error(val message: String) : MessageUiEvent()
    data class MessageSelected(val message: MessageChat) : MessageUiEvent()
}