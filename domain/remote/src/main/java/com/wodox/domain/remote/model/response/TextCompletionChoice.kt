package com.starnest.domain.remote.model.response

class TextCompletionChoice(
    var message: TextCompletionMessageResponse? = null,
    var delta: TextCompletionMessageResponse? = null,
)  {
}

