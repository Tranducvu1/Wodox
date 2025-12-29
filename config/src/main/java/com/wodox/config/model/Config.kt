package com.wodox.config.model

import com.wodox.config.BuildConfig

object Config {
    object InAppProduct {
        const val WEEKLY = "weekly"
        const val WEEKLY_TRIAL = "weekly2"
        const val YEARLY = "yearly"
        const val LIFE_TIME = "lifetime"

        object Pack {
            const val PACK_500 = ""
        }
    }

    object Folder {
        const val AI_COMPANION = "Companion"
        const val BACKGROUND_CHAT_SEVER = "backgrounds/Chat"
        const val AI_COMPANION_SEVER = "companion/background"
    }


    object URL {
        const val S3_URL = "https://keyboard.startnest.uk/aikeyboard"
        const val KEYBOARD_S3_URL = "https://widget.startnest.uk"
    }

    object API {
        const val BASE_BACKUP_URL = BuildConfig.BASE_BACKUP_URL
        const val BASE_URL = BuildConfig.BASE_URL
        const val KID = BuildConfig.KID

    }

    object ChatMessage {
        const val TOTAL_CHAT_MESSAGE_PER_DAY = 50
    }

    const val SCALE_MAX_SIZE = 400
    const val MAX_RESULT_IMAGE_SIZE = 500
    const val COMPRESS_IMAGE_QUALITY = 75
    const val MAX_SERVER_IMAGE_SIZE = 400f
}