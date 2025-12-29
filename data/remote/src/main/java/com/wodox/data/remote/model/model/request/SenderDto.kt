package com.wodox.data.remote.model.model.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
enum class SenderDto(val value: String) {
    @SerialName("system")
    SYSTEM("system"),

    @SerialName("user")
    USER("user"),

    @SerialName("assistant")
    ASSISTANT("assistant"),

    @SerialName("app")
    APP("app"),
}
