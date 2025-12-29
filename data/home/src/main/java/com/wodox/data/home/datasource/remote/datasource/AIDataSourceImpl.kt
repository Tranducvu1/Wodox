package com.wodox.data.home.datasource.remote.datasource

import android.util.Log
import com.google.gson.Gson
import com.wodox.data.home.datasource.remote.api.ApiService
import com.wodox.data.home.datasource.remote.model.request.TextCompletionMessageRequestDto
import com.wodox.data.home.datasource.remote.model.request.TextCompletionRequestDto
import javax.inject.Inject

class AIDataSourceImpl @Inject constructor(
    private val openAIApi: ApiService,
    private val gson: Gson
) : AIChatDataSource {
    override suspend fun simpleAiChat(
        input: String,
        systemPrompt: String?
    ): String? {
        return try {
            val messages = arrayListOf<TextCompletionMessageRequestDto>()

            if (!systemPrompt.isNullOrEmpty()) {
                messages.add(TextCompletionMessageRequestDto("system", systemPrompt))
            }

            messages.add(TextCompletionMessageRequestDto("user", input))

            val request = TextCompletionRequestDto(
                model = "gpt-3.5-turbo",
                messages = messages,
                temperature = 0.7,
                maxTokens = 1000
            )

            val headers = hashMapOf("Content-Type" to "application/json")

            val response = openAIApi.createChatCompletionV2(headers, request).execute()

            response.body()?.data?.choices?.firstOrNull()?.message?.content
        } catch (e: Exception) {
            Log.e("AIChat", "AI error: ${e.message}", e)
            null
        }
    }
}