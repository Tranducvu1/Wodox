package com.starnest.domain.remote.model.response


data class TextCompletionData(
    var id: String? = null,
    var choices: List<TextCompletionChoice> = arrayListOf()
)  {
    val output: String?
        get() = choices.firstOrNull()?.message?.content ?: choices.firstOrNull()?.delta?.content


    fun getParsedOutput(): String {
        val output = output ?: return ""

        return getValues(output)
    }

    private fun getValues(output: String): String {
        val pattern = "(?s)```(.+)```".toRegex()
        return pattern.find(output)?.groupValues?.getOrNull(1) ?: output
    }
}
