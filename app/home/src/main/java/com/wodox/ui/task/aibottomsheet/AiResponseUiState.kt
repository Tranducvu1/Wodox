package com.wodox.ui.task.aibottomsheet

import com.wodox.domain.remote.model.request.ChatMessage

data class AiResponseUiState (
    val isLoading: Boolean = false,
    val aiResponse: String? = null,
    val error: String? = null,
    val messages: List<ChatMessage> = emptyList()
)