package com.wodox.chat.model


import android.content.Context
import com.wodox.domain.chat.model.UsageKey


enum class AdvanceToolType(override val key: String) : UsageKey {
    MATH("math"),

    UPLOAD_ASK("upload_and_ask"),

    HOME_WORK("home_work"),

    AI_KEYBOARD("ai_keyboard"),

    BUBBLE_AI("bubble_ai"),

    EMAIL("email"),

    REAL_TIME_SEARCH("real_time_search"),

    OCR("ocr"),

    ASK_URL("ask_url"),

    OTHER("other");

    val isEnableSuggestion: Boolean
        get() = arrayListOf(
            MATH, HOME_WORK
        ).contains(this)

    fun getName(context: Context): String {
        return name
    }
}