package com.wodox.data.remote.repository

import android.app.Application
import android.content.Context
import com.wodox.domain.chat.model.ChatModel
import com.wodox.data.common.datasource.usageConfig
import com.starnest.data.common.extension.isPremium
import com.starnest.data.common.util.PromptUtil
import com.wodox.data.remote.model.datasource.AIDataSource
import com.wodox.data.remote.model.mapper.TextCompletionRequestMapper
import com.wodox.data.remote.model.mapper.TextCompletionResponseMapper
import com.wodox.data.remote.model.model.request.SenderDto
import com.wodox.data.remote.util.TextCompletionUtil
import com.starnest.domain.remote.model.TextCompletionInput
import com.wodox.domain.remote.model.request.ChatMessage
import com.starnest.domain.remote.model.request.TextCompletionMessage
import com.starnest.domain.remote.model.request.TextCompletionRequest
import com.starnest.domain.remote.model.response.ResponseFormat
import com.wodox.domain.remote.model.response.TextCompletionResponse
import com.wodox.domain.remote.repository.AIChatRepository
import com.wodox.core.extension.toArrayList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.InternalSerializationApi
import javax.inject.Inject

class AIChatRepositoryImpl @Inject constructor(
    private val app: Application,
    private val aiChatDataSource: AIDataSource,
    private val textCompletionResponseMapper: TextCompletionResponseMapper,
    private val textCompletionRequestMapper: TextCompletionRequestMapper
) : AIChatRepository {

    override fun chatWithStream(
        context: Context,
        groupId: String,
        input: TextCompletionInput,
        messages: List<ChatMessage>,
        isRealTimeSearch: Boolean
    ): Flow<Pair<String, TextCompletionResponse>> {
        val headers = createHeaders(
            context = context,
            isHasAttachment = messages.any { message ->
                message.content.any { it.isImageUrl }
            }
        )

        val request = TextCompletionUtil.buildChatRequest(
            context,
            input = input,
            messages = messages
        )

        return aiChatDataSource.chatWithStream(
            groupId = groupId,
            headers = headers,
            request = request
        ).map { Pair(it.first, textCompletionResponseMapper.mapToDomain(it.second)) }
    }

    private fun createHeaders(context: Context, isHasAttachment: Boolean): HashMap<String, String> {
        val usageConfig = context.usageConfig

        val headers = HashMap<String, String>()

        val chatModel = ChatModel.GEMINI
        headers["isVip"] = "${context.isPremium == true}"
        headers["chatModel"] = chatModel.model
        headers["messageRequest"] = "${usageConfig.chatMessageDaily.messageRequest}"
        val totalMessageRequest = usageConfig.chatModelUsage.getUsage(chatModel)

        headers["totalMessageRequest"] = "$totalMessageRequest"

        return headers
    }

    @OptIn(InternalSerializationApi::class)
    override suspend fun textCompletions(
        context: Context,
        request: TextCompletionRequest,
        featureRequest: String
    ): TextCompletionResponse {
        val headers = createHeaders(context, false)

        return aiChatDataSource.textCompletions(
            headers = headers,
            request = textCompletionRequestMapper.mapToDto(request)
        ).let {
            textCompletionResponseMapper.mapToDomain(it)
        }
    }

    override suspend fun getSuggestions(
        histories: List<ChatMessage>,
    ): ArrayList<String> {
        val result = ArrayList<String>()
        try {
            val messages = histories.toArrayList().map {
                TextCompletionMessage(
                    role = it.role,
                    content = it.content.firstOrNull()?.content ?: ""
                )
            }.toArrayList().apply {
                this.add(
                    TextCompletionMessage(
                        role = SenderDto.SYSTEM.value,
                        content = PromptUtil.getSuggestionPrompt()
                    )
                )
            }

            val isVip = app.isPremium
            val chatModel = ChatModel.GEMINI.model

            val request = TextCompletionRequest.Companion.builder()
                .setModel(chatModel)
                .setIsVip(isVip)
                .setMaxToken(2000)
                .setMessages(messages)
                .setResponseFormat(ResponseFormat.Companion.getSuggestionResponseFormat)
                .build()

            val headers = HashMap<String, String>()
            headers["isVip"] = "$isVip"
            headers["chatModel"] = chatModel
            headers["isUserInAdMode"] = "${true}"
            headers["forceUseGemini"] = "${true}"


            return aiChatDataSource.getSuggestions(
                headers = headers,
                request = textCompletionRequestMapper.mapToDto(request)
            )

        } catch (_: Exception) {
            return arrayListOf()
        }

        return result
    }
}