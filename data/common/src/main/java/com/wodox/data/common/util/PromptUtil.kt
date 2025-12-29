package com.starnest.data.common.util


object PromptUtil {
    fun getSuggestionPrompt(): String {
        return "Based on the user input and your response if have, generate 2 related suggested questions have to closely related to the content, without extra words. Note that return suggestion language is the language of latest user's message, no return different languages. No return different languages. Generate each suggestion question with a maximum length of 60 characters"
    }

}