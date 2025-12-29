package com.wodox.data.remote.model.model.response


data class TextCompletionResponseDto(
    val data: TextCompletionDataDto? = null,
    val error: String? = null,
) {
}