package com.starnest.domain.remote.model.request

import com.starnest.domain.remote.model.response.ResponseFormat

data class TextCompletionRequest(
    var messages: List<TextCompletionMessage>? = null,
    var responseFormat: ResponseFormat? = null,
    var expertId: Int? = null,
    var advanceToolType: String? = null,
    var suggestionId: Int? = null,
    var assistantId: Int? = null,
    var isVip: Boolean? = null,
    var maxTokens: Int? = null,
)  {
    companion object {
        fun builder(): Builder {
            return Builder()
        }
    }

    class Builder {
        var isVip: Boolean? = null
        var assistantId: Int? = null
        var suggestionId: Int? = null
        var advanceToolType: String? = null
        var expertId: Int? = null
        var model: String? = null
        var messages: List<TextCompletionMessage>? = null
        var stream: Boolean? = null
        var responseFormat: ResponseFormat? = null
        var maxTokens: Int? = null

        fun setAdvanceToolType(advanceToolType: String?): Builder {
            this.advanceToolType = advanceToolType
            return this
        }

        fun setExpertId(expertId: Int?): Builder {
            this.expertId = expertId
            return this
        }

        fun setSuggestionId(suggestionId: Int?): Builder {
            this.suggestionId = suggestionId
            return this
        }

        fun setAssistantId(assistantId: Int?): Builder {
            this.assistantId = assistantId
            return this
        }

        fun setModel(model: String?): Builder {
            return this
        }


        fun setMessages(messages: List<TextCompletionMessage>?): Builder {
            this.messages = messages
            return this
        }

        fun setStream(stream: Boolean?): Builder {
            this.stream = stream
            return this
        }

        fun setResponseFormat(format: ResponseFormat?): Builder {
            this.responseFormat = format
            return this
        }


        fun setMaxToken(maxTokens: Int): Builder {
            this.maxTokens = maxTokens
            return this
        }

        fun setIsVip(isVip: Boolean): Builder {
            this.isVip = isVip
            return this
        }


        fun build() = TextCompletionRequest(
            isVip = isVip,
            suggestionId = suggestionId,
            expertId = expertId,
            advanceToolType = advanceToolType,
            assistantId = assistantId,
            messages = messages,
            responseFormat = responseFormat,
            maxTokens = maxTokens
        )
    }
}