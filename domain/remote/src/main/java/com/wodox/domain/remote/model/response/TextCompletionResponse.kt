package com.wodox.domain.remote.model.response

import com.starnest.domain.remote.model.response.TextCompletionData

data class TextCompletionResponse(
    val data: TextCompletionData? = null,
    val error: String? = null,
) {
}