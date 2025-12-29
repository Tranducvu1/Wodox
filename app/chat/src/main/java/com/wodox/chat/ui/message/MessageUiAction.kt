package com.wodox.chat.ui.message

import com.wodox.domain.chat.model.local.MessageChat
import java.util.UUID

sealed class MessageUiAction {

    data class SendMessage(val text: String) : MessageUiAction()

    data class UpdateMessage(val messageId: MessageChat) : MessageUiAction()

    data class DeleteMessage(val messageId: UUID) : MessageUiAction()

    data class SearchMessages(val query: String) : MessageUiAction()

    object ClearMessages : MessageUiAction()

    object LoadFriendUser : MessageUiAction()
}