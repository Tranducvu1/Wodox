package com.wodox.data.remote.model.datasource

import com.wodox.data.remote.model.model.request.ChatRequestDto
import com.wodox.data.remote.model.model.request.TextCompletionRequestDto
import com.wodox.data.remote.model.model.response.TextCompletionResponseDto
import kotlinx.coroutines.flow.Flow

interface AIDataSource {
    fun chatWithStream(
        groupId: String,
        headers: Map<String, String>,
        request: ChatRequestDto
    ): Flow<Pair<String, TextCompletionResponseDto>>

    suspend fun textCompletions(
        headers: Map<String, String>,
        request: TextCompletionRequestDto
    ): TextCompletionResponseDto

    suspend fun getSuggestions(
        headers: Map<String, String>,
        request: TextCompletionRequestDto
    ): ArrayList<String>
}