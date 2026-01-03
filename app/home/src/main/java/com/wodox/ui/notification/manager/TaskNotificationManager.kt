package com.wodox.ui.notification.manager

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.wodox.domain.home.model.local.Task
import com.wodox.ui.task.taskdetail.TaskDetailFragment
import java.util.Date
import java.util.concurrent.TimeUnit

object TaskNotificationManager {
    private const val NOTIFICATION_CHANNEL_ID = "task_deadline_channel"
    private const val NOTIFICATION_CHANNEL_NAME = "Task Deadline Alerts"
    private const val CHANNEL_DESCRIPTION = "Notifications for tasks nearing deadline"
    private const val TAG = "TaskNotificationManager"

    fun createNotificationChannel(context: Context) {
        Log.d(TAG, "Creating notification channel...")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                NOTIFICATION_CHANNEL_NAME,
                importance
            ).apply {
                description = CHANNEL_DESCRIPTION
                enableVibration(true)
                enableLights(true)
            }

            val notificationManager = context.getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
            Log.d(TAG, "✅ Notification channel created")
        }
    }

    fun checkAndNotifyDeadlineTasks(context: Context, tasks: List<Task>) {
        Log.d(TAG, "")
        Log.d(TAG, "┌──────────────────────────────────────")
        Log.d(TAG, "│ 🔍 checkAndNotifyDeadlineTasks()")
        Log.d(TAG, "│    Total tasks: ${tasks.size}")
        Log.d(TAG, "└──────────────────────────────────────")

        var notifiedCount = 0
        tasks.forEachIndexed { index, task ->
            Log.d(TAG, "   [$index] Checking '${task.title}'...")

            if (isTaskDeadlineApproaching(task)) {
                Log.d(TAG, "        ✅ Deadline approaching → SENDING NOTIFICATION")
                sendDeadlineNotification(context, task)
                notifiedCount++
            } else {
                Log.d(TAG, "        ⏭️ Not approaching deadline → SKIPPED")
            }
        }

        Log.d(TAG, "")
        Log.d(TAG, "═══════════════════════════════════════")
        Log.d(TAG, "📊 SUMMARY: $notifiedCount/${ tasks.size} tasks notified")
        Log.d(TAG, "═══════════════════════════════════════")
        Log.d(TAG, "")
    }

    private fun isTaskDeadlineApproaching(task: Task): Boolean {
        val dueDate = task.dueAt ?: return false
        val now = Date()

        val timeDifference = dueDate.time - now.time
        val daysUntilDeadline = TimeUnit.MILLISECONDS.toDays(timeDifference)
        val hoursUntilDeadline = TimeUnit.MILLISECONDS.toHours(timeDifference)

        Log.d(TAG, "        Details:")
        Log.d(TAG, "         • Status: ${task.status?.name}")
        Log.d(TAG, "         • Days: $daysUntilDeadline")
        Log.d(TAG, "         • Hours: $hoursUntilDeadline")
        Log.d(TAG, "         • In range 0-24h: ${hoursUntilDeadline in 0..24}")
        Log.d(TAG, "         • Not DONE: ${task.status?.name != "DONE"}")

        return hoursUntilDeadline in 0..24 && task.status?.name != "DONE"
    }

    private fun sendDeadlineNotification(context: Context, task: Task) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notificationId = task.id.hashCode()
        val intent = Intent(context, TaskDetailFragment::class.java).apply {
            putExtra("TASK_ID", task.id.toString())
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            notificationId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val hoursRemaining = getHoursUntilDeadline(task.dueAt!!)

        val notification = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(com.wodox.resources.R.drawable.ic_tag)
            .setContentTitle("⚠️ Task sắp hết hạn")
            .setContentText("'${task.title}' còn ${hoursRemaining} giờ nữa")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("Task '${task.title}' của bạn sắp hết hạn.\nVui lòng hoàn thành trong thời gian sớm nhất.")
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setVibrate(longArrayOf(0, 500, 250, 500))
            .build()

        notificationManager.notify(notificationId, notification)
    }

    private fun getHoursUntilDeadline(dueDate: Date): Long {
        val now = Date()
        val timeDifference = dueDate.time - now.time
        return TimeUnit.MILLISECONDS.toHours(timeDifference)
    }
}