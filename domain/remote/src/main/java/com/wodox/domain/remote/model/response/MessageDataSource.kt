package com.starnest.domain.remote.model.response

import java.util.regex.Pattern

data class MessageDataSource(
    val title: String = "",
    val link: String = "",
    val snippet: String = "",
    val date: String = "",
    var imageUrl: String? = null
) {

    val website: String
        get() {
            val regex = "^(?:https?://)?(?:www\\.)?([^:/\\s]+)"

            val pattern = Pattern.compile(regex)
            val matcher = pattern.matcher(link)

            return if (matcher.find()) {
                matcher.group(1) ?: ""
            } else {
                ""
            }
        }
}