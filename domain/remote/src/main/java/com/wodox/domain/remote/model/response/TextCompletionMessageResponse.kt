package com.starnest.domain.remote.model.response


data class TextCompletionMessageResponse(
    var role: String = "",
    var content: String = "",
    var name: String? = null,
)  {

}