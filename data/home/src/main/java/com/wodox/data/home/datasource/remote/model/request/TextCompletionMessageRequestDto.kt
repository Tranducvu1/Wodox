package com.wodox.data.home.datasource.remote.model.request

import com.google.gson.annotations.SerializedName

data class TextCompletionMessageRequestDto(
    @SerializedName("role")
    val role: String, // "system", "user", "assistant"

    @SerializedName("content")
    val content: String
)