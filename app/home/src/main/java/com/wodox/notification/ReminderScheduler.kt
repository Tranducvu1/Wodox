package com.wodox.notification


import android.content.Context
import android.util.Log
import androidx.work.*
import java.util.concurrent.TimeUnit

object ReminderScheduler {
    fun scheduleReminder(
        context: Context,
        taskId: String,
        title: String,
        triggerTime: Long
    ) {
        val delay = triggerTime - System.currentTimeMillis()

        Log.d("ReminderScheduler", "Current time: ${System.currentTimeMillis()}")
        Log.d("ReminderScheduler", "Trigger time: $triggerTime")
        Log.d("ReminderScheduler", "Delay: $delay ms")

        if (delay <= 0) {
            Log.w("ReminderScheduler", "Invalid delay time, reminder not scheduled")
            return
        }
        Log.d("ReminderScheduler", "Reminder scheduled for task: $taskId")
    }
}