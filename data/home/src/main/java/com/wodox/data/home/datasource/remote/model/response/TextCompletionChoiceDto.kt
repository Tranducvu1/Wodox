package com.wodox.data.home.datasource.remote.model.response

import com.google.gson.annotations.SerializedName


data class TextCompletionChoiceDto(
    @SerializedName("index")
    val index: Int?,

    @SerializedName("message")
    val message: TextCompletionMessageDto?,

    @SerializedName("finish_reason")
    val finishReason: String?
)