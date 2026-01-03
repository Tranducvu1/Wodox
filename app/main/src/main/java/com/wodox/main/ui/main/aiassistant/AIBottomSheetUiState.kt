package com.wodox.main.ui.main.aiassistant

import com.wodox.domain.remote.model.request.ChatMessage


data class AIBottomSheetUiState (
    val isLoading: Boolean = false,
    val aiResponse: String? = null,
    val error: String? = null,
    val messages: List<ChatMessage> = emptyList()
)