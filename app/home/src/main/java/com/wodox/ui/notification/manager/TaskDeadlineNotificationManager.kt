package com.wodox.ui.notification.manager


import android.content.Context
import android.util.Log
import com.wodox.domain.home.model.local.ScheduleModel
import com.wodox.extension.cancelNotification
import com.wodox.extension.setupNotification
import com.wodox.model.Constants
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

object TaskDeadlineNotificationManager {
    private const val TAG = "TaskDeadlineNotificationManager"


    fun startDeadlineCheck(context: Context) {
        val nowOriginal = Calendar.getInstance()
        val nowTrigger = Calendar.getInstance().apply {
            add(Calendar.MINUTE, 1)
        }

        val scheduleModel = ScheduleModel(
            id = Constants.RequestCodes.TASK_DEADLINE_CHECK,
            triggerDate = nowTrigger
        )

        val sdf = SimpleDateFormat("HH:mm:ss dd/MM/yyyy", Locale.getDefault())
        Log.d(TAG, "   Current time: ${sdf.format(nowOriginal.time)}")
        Log.d(TAG, "   Trigger time: ${sdf.format(nowTrigger.time)}")

        context.setupNotification<TaskDeadlineReceiver>(
            calendar = scheduleModel.triggerDate,
            isRepeatable = false,
            requestCode = scheduleModel.id,
            Constants.Intents.TASK_DEADLINE to scheduleModel
        )
    }

    fun stopDeadlineCheck(context: Context) {
        Log.d(TAG, "")
        Log.d(TAG, "┌─────────────────────────────────────────")
        Log.d(TAG, "│ 🛑 stopDeadlineCheck() CALLED")
        Log.d(TAG, "└─────────────────────────────────────────")

        try {
            val requestCode = Constants.RequestCodes.TASK_DEADLINE_CHECK
            Log.d(TAG, "   Cancelling RequestCode: $requestCode")

            context.cancelNotification<TaskDeadlineReceiver>(
                requestCode = requestCode
            )
            Log.d(TAG, "   ✅ Alarm cancelled successfully!")
            Log.d(TAG, "")
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error in stopDeadlineCheck", e)
        }
    }
}