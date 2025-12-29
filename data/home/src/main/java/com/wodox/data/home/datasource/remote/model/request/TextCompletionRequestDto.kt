package com.wodox.data.home.datasource.remote.model.request

import com.google.gson.annotations.SerializedName

data class TextCompletionRequestDto(
    @SerializedName("model")
    val model: String = "gpt-3.5-turbo",

    @SerializedName("messages")
    val messages: List<TextCompletionMessageRequestDto>,

    @SerializedName("temperature")
    val temperature: Double = 0.7,

    @SerializedName("max_tokens")
    val maxTokens: Int? = null,

    @SerializedName("stream")
    val stream: Boolean = false
)