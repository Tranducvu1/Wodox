package com.wodox.data.remote.model.model.request

import android.annotation.SuppressLint
import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable


@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class ChatRequestDto(
    @SerializedName("messages")
    var messages: List<ChatMessageDto>? = null,
    @SerializedName("response_format")
    var responseFormat: ResponseFormatDto? = null,
    @SerializedName("expertId")
    var expertId: Int? = null,
    @SerializedName("advanceToolType")
    var advanceToolType: String? = null,
    @SerializedName("suggestionId")
    var suggestionId: Int? = null,
    @SerializedName("assistantId")
    var assistantId: Int? = null,
    @SerializedName("isVip")
    var isVip: Boolean = false,
) : java.io.Serializable {
    companion object {
        fun builder(): Builder {
            return Builder()
        }
    }

    class Builder {
        var isVip: Boolean? = null
        var model: String? = null
        var suggestionId: Int? = null
        var assistantId: Int? = null
        var advanceToolType: String? = null
        var expertId: Int? = null
        var messages: List<ChatMessageDto>? = null
        var stream: Boolean? = null
        var responseFormat: ResponseFormatDto? = null
        var maxTokens: Int? = null

        fun setModel(model: String?): Builder {
            return this
        }

        fun setIsVip(isVip: Boolean): Builder {
            this.isVip = isVip
            return this
        }

        fun setMessages(messages: List<ChatMessageDto>?): Builder {
            this.messages = messages
            return this
        }

        fun setAdvanceToolType(advanceToolType: String?): Builder {
            this.advanceToolType = advanceToolType
            return this
        }

        fun setExpertId(expertId: Int?): Builder {
            this.expertId = expertId
            return this
        }

        fun setAssistantId(assistantId: Int?): Builder {
            this.assistantId = assistantId
            return this
        }

        fun setSuggestionId(suggestionId: Int?): Builder {
            this.suggestionId = suggestionId
            return this
        }

        fun setStream(stream: Boolean?): Builder {
            this.stream = stream
            return this
        }

        fun setResponseFormat(format: ResponseFormatDto?): Builder {
            this.responseFormat = format
            return this
        }

        fun setMaxToken(maxTokens: Int): Builder {
            this.maxTokens = maxTokens
            return this
        }


        fun build() = ChatRequestDto(
            isVip = isVip ?: false,
            messages = messages,
            suggestionId = suggestionId,
            expertId = expertId,
            assistantId = assistantId,
            advanceToolType = advanceToolType,
            responseFormat = responseFormat,
        )
    }
}