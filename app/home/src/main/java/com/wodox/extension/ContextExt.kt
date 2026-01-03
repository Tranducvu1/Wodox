package com.wodox.extension

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Parcelable
import android.util.Log
import java.util.Calendar

inline fun <reified T : BroadcastReceiver> Context.setupNotification(
    calendar: Calendar,
    isRepeatable: Boolean = false,
    requestCode: Int,
    vararg extras: Pair<String, Parcelable>
) {
    try {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, T::class.java).apply {
            extras.forEach { (key, value) ->
                putExtra(key, value)
            }
        }

        val pendingIntent = PendingIntent.getBroadcast(
            this,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val triggerTime = calendar.timeInMillis

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (alarmManager.canScheduleExactAlarms()) {
                scheduleAlarm(alarmManager, triggerTime, pendingIntent, isRepeatable)
            } else {
                Log.e("NotificationExt", "Cannot schedule exact alarms - permission denied")
            }
        } else {
            scheduleAlarm(alarmManager, triggerTime, pendingIntent, isRepeatable)
        }

        Log.d("NotificationExt", "Notification scheduled for ${T::class.simpleName} at $triggerTime")
    } catch (e: Exception) {
        Log.e("NotificationExt", "Error scheduling notification", e)
    }
}

fun scheduleAlarm(
    alarmManager: AlarmManager,
    triggerTime: Long,
    pendingIntent: PendingIntent,
    isRepeatable: Boolean
) {
    if (isRepeatable) {
        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            triggerTime,
            AlarmManager.INTERVAL_FIFTEEN_MINUTES,
            pendingIntent
        )
    } else {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerTime,
                pendingIntent
            )
        } else {
            alarmManager.setExact(
                AlarmManager.RTC_WAKEUP,
                triggerTime,
                pendingIntent
            )
        }
    }
}

inline fun <reified T : BroadcastReceiver> Context.cancelNotification(requestCode: Int) {
    try {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, T::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            this,
            requestCode,
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )

        pendingIntent?.let {
            alarmManager.cancel(it)
            it.cancel()
            Log.d("NotificationExt", "Cancelled notification for ${T::class.simpleName}")
        }
    } catch (e: Exception) {
        Log.e("NotificationExt", "Error cancelling notification", e)
    }
}