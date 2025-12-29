package com.starnest.data.common.util

import kotlinx.serialization.json.Json

class JsonUtil {
    companion object {
        fun defaultJson(): Json {
            return Json {
                ignoreUnknownKeys = true
            }
        }

        inline fun<reified T> parse(value: String): T {
            return defaultJson().decodeFromString(value)
        }
    }
}