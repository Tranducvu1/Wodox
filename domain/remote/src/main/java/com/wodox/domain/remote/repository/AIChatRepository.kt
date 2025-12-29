package com.wodox.domain.remote.repository

import android.content.Context
import com.starnest.domain.remote.model.TextCompletionInput
import com.wodox.domain.remote.model.request.ChatMessage
import com.starnest.domain.remote.model.request.TextCompletionRequest
import com.wodox.domain.remote.model.response.TextCompletionResponse
import kotlinx.coroutines.flow.Flow

interface AIChatRepository {
    fun chatWithStream(
        context: Context,
        groupId: String,
        input: TextCompletionInput,
        messages: List<ChatMessage>,
        isRealTimeSearch: Boolean = false
    ): Flow<Pair<String, TextCompletionResponse>>

    suspend fun textCompletions(
        context: Context,
        request: TextCompletionRequest,
        featureRequest: String,
    ): TextCompletionResponse

    suspend fun getSuggestions(
        histories: List<ChatMessage>,
    ): ArrayList<String>
}