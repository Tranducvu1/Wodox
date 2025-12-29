package com.wodox.domain.remote.model.request

import com.starnest.domain.common.model.Sender
import com.starnest.domain.remote.model.request.ChatMessageContent

data class ChatMessage(
    var role: String = "",
    var content: List<ChatMessageContent> = arrayListOf()
) {

    val sender: Sender
        get() = Sender.entries.firstOrNull { it.value == role } ?: Sender.ASSISTANT

    val compareContent: String
        get() = content.joinToString(",") { it.content ?: "" }

}