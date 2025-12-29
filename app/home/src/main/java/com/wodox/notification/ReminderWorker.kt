package com.wodox.notification


import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters

class ReminderWorker(
    private val context: Context,
    params: WorkerParameters
) : Worker(context, params) {

    override fun doWork(): Result {
        Log.d("ReminderWorker", "Worker started")

        val taskId =    inputData.getString("task_id") ?: return Result.failure()
        val title = inputData.getString("task_title") ?: "Task Reminder"

        Log.d("ReminderWorker", "Showing notification for: $title")

        NotificationHelper.showTaskNotification(context, taskId, title)
        return Result.success()
    }
}
