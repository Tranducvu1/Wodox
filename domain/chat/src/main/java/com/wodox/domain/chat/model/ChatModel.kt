package com.wodox.domain.chat.model

interface UsageKey {
    val key: String
}

enum class ChatModel(
    override val key: String,
    val model: String,
    val limited: Int = Int.MAX_VALUE
) : UsageKey {
    GEMINI(
        key = "gemini_lite",
        model = "gemini_lite"
    );

    val displayName: String
        get() = when (this) {
            GEMINI -> "Gemini 2.0 Flash-Lite"
        }

    val chatModel: String
        get() = when (this) {
            GEMINI -> "gemini_lite"
        }
}
