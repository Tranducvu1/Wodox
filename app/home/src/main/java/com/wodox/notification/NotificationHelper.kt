package com.wodox.notification


import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.core.app.NotificationCompat
import com.wodox.home.R
import com.wodox.ui.task.TaskActivity
import com.wodox.model.Constants

object NotificationHelper {

    private const val CHANNEL_ID = "task_reminder_channel"

    fun createChannel(context: Context) {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channel = NotificationChannel(
            CHANNEL_ID,
            "Task Reminder",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Reminder for your tasks"
            enableLights(true)
            lightColor = Color.GREEN
        }

        manager.createNotificationChannel(channel)
    }

    fun showTaskNotification(context: Context, taskId: String, title: String) {
        val intent = Intent(context, TaskActivity::class.java).apply {
            putExtra(Constants.Intents.TASK_ID, taskId)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            taskId.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(com.wodox.resources.R.drawable.ic_notification)
            .setContentTitle("Reminder")
            .setContentText(title)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(taskId.hashCode(), notification)
    }
}
