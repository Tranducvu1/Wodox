package com.wodox.data.remote.model.model.request

import android.annotation.SuppressLint
import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable


@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class TextCompletionRequestDto(
    @SerializedName("messages")
    var messages: List<TextCompletionMessageDto>? = null,
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
    var isVip: Boolean? = null,
    @SerializedName("max_tokens")
    var maxTokens: Int? = null
) : java.io.Serializable {

}