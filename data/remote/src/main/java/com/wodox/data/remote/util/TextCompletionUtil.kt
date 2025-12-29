package com.wodox.data.remote.util

import android.content.Context

import com.starnest.data.common.extension.findLastIndexOfDot
import com.starnest.data.common.extension.template
import com.wodox.data.remote.model.model.request.ChatMessageContentDto
import com.wodox.data.remote.model.model.request.ChatMessageContentImageDto
import com.wodox.data.remote.model.model.request.ChatMessageDto
import com.wodox.data.remote.model.model.request.ChatRequestDto
import com.wodox.data.remote.model.model.request.SenderDto
import com.starnest.domain.remote.model.TextCompletionInput
import com.wodox.core.app.AbstractApplication
import com.wodox.core.extension.toArrayList
import com.wodox.core.extension.toBase64
import com.wodox.domain.remote.model.request.ChatMessage
import java.io.File

object TextCompletionUtil {
    fun truncateInput(
        context: Context, textCompletionInput: TextCompletionInput, isRefresh: Boolean = false
    ) {
        var input = textCompletionInput.input
        val maxInputLength = 15000
        if (input.length > maxInputLength) {
            input = input.take(maxInputLength)
            val indexOfLastDot = input.findLastIndexOfDot(maxInputLength - 500)
            if (indexOfLastDot != -1) input = input.take(indexOfLastDot + 1)
            textCompletionInput.isTruncatedInput = true

            if (isRefresh) return
        }
    }

    private fun buildAskContent(
        context: Context, input: TextCompletionInput
    ): String {
        val content = "{input}"

        val params = HashMap<String, String?>()
        params[Params.INPUT] = input.input
//        params[Params.TONE] = LanguageMapper.translate(context, tone = tone?.tone)
//        params[Params.LANGUAGE] = language?.languageName
//        params[Params.PROMPT] = replyBot?.prompt
//        params[Params.KNOWLEDGE_SOURCE] = replyBot?.generateKnowledgeSource()

        return content.template(params)
    }

    fun buildChatRequest(
        context: Context, input: TextCompletionInput, messages: List<ChatMessage>
    ): ChatRequestDto {
        val messageRequests = messages.map {
            ChatMessageDto().apply {
                this.role = if (it.role == "app") {
                    "assistant"
                } else {
                    it.role
                }
                this.content = it.content.map {
                    ChatMessageContentDto(
                        type = it.type,
                        text = it.text,
                        imageUrl = it.imageUrl?.let {
                            ChatMessageContentImageDto(
                                url = it.url
                            )
                        },
                    )
                }
            }
        }.toArrayList().apply {
            val content = buildAskContent(
                context = context, input = input
            )

            if (content.trim() != lastOrNull()?.content?.firstOrNull()?.text?.trim()) {
                this.add(
                    toMessageRequest(
                        context = context, input = input
                    )
                )
            }
//            else if (isRealTimeSearch) {
//
//                this.lastOrNull()?.apply {
//                    val sendContent = this.content.firstOrNull()?.content
//
//                    if (sendContent != null){
//                        this.content.firstOrNull()?.text = "$content. Also provide some related website"
//                    }
//                }
//            }
        }

//        val overrideModel =
//            if (messageRequests.any { it.content.any { item -> item.type == "image_url" } }) {
//                GPTModel.GPT_3_5_Turbo.model
//            } else {
//                model
//            }
        val isVip = (context.applicationContext as? AbstractApplication)?.isPremium ?: false

        return ChatRequestDto.builder().setExpertId(input.expertId)
            .setAssistantId(input.assistantId).setAdvanceToolType(input.advanceToolType)
            .setSuggestionId(input.suggestionId).setIsVip(isVip).setMaxToken(2000).setMessages(
                messageRequests
            ).build()
    }

    private fun toMessageRequest(
        context: Context, input: TextCompletionInput
    ): ChatMessageDto {
        return ChatMessageDto().apply {
            this.role = SenderDto.USER.value
            val content = ArrayList<ChatMessageContentDto>()
            if (input.attachments != null && input.attachments!!.isNotEmpty()) {
                content.addAll(
                    input.attachments!!.map {
                        ChatMessageContentDto(
                            type = "image_url", imageUrl = ChatMessageContentImageDto(
                                url = "data:image/jpeg;base64,${File(it).toBase64()}"
                            )
                        )
                    })
            }
            if (input.input.isNotEmpty()) {
                val askContent = buildAskContent(
                    context = context, input = input
                )

                content.add(
                    ChatMessageContentDto(
                        type = "text", text = askContent
                    )
                )
            }
            this.content = content
        }
    }

    object Params {
        const val INPUT = "{input}"
        const val TONE = "{tone}"
        const val LANGUAGE = "{language}"
        const val PROMPT = "{prompt}"
        const val KNOWLEDGE_SOURCE = "{knowledgeSource}"
    }
}