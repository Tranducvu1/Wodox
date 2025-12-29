package com.starnest.domain.common.model

enum class Sender(val value: String) {
    SYSTEM("system"),

    USER("user"),

    ASSISTANT("assistant"),

    APP("app"),
}