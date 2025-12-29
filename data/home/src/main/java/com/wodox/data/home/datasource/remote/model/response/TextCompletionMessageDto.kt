package com.wodox.data.home.datasource.remote.model.response

import com.google.gson.annotations.SerializedName

data class TextCompletionMessageDto(
    @SerializedName("role")
    val role: String?,

    @SerializedName("content")
    val content: String?
)