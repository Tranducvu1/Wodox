package com.wodox.data.home.datasource.remote.model.response

import com.google.gson.annotations.SerializedName

data class TextCompletionDataDto(
    @SerializedName("id")
    val id: String?,

    @SerializedName("choices")
    val choices: List<TextCompletionChoiceDto>?,

    @SerializedName("model")
    val model: String?,

    @SerializedName("usage")
    val usage: UsageDto?
)