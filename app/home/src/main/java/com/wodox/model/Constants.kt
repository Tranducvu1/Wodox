package com.wodox.model

object Constants {

    const val DAILY_CHANNEL_ID = "Daily Chipis Notification"

    const val SCRIPT_CHANNEL_ID = "Chipis Notification"
    const val SCRIPT_CHANNEL_DESCRIPTION = "Chipis Notification"

    object Intents {
        const val TASK = "TASK"

        const val TASK_DEADLINE = "TASK_DEADLINE"

        const val SUB_TASK = "SUB_TASK"
        const val TEXT_FORMAT ="TEXT_FORMAT"
        const val TASK_ID = "TASK_ID"

        const val NOTIFICATION = "NOTIFICATION"

        const val NOTIFICATION_SCRIPT_TYPE = "NOTIFICATION_SCRIPT_TYPE"

        const val IS_FROM_NOTIFICATION = "IS_FROM_NOTIFICATION"

    }

    object RequestCodes {
        const val TASK_DEADLINE_CHECK = 2001
    }

}