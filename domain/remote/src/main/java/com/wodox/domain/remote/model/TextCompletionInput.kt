package com.starnest.domain.remote.model


enum class TypeAiMenuType(
    val value: String,
    val outputLimited4o: Int? = null,
    val outputLimited4oMini: Int? = null,
) {
    ASK_AI(
        value = "ask_ai",
        outputLimited4o = 2000,
        outputLimited4oMini = 2000
    )
}

data class TextCompletionInput(
    var input: String = "",
    var type: TypeAiMenuType = TypeAiMenuType.ASK_AI,
    var isTruncatedInput: Boolean = false,
    var attachments: List<String>? = null,
    var expertId: Int? = null,
    var assistantId: Int? = null,
    var advanceToolType: String? = null,
    var suggestionId: Int? = null,
) {

}