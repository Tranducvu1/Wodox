package com.wodox.ui.notification.manager

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.PowerManager
import android.util.Log
import com.wodox.core.extension.ensureBackgroundThread
import com.wodox.core.extension.serializable
import com.wodox.domain.home.model.local.ScheduleModel
import com.wodox.domain.home.repository.SettingsRepository
import com.wodox.domain.home.usecase.task.GetAllTasksByUserUseCase
import com.wodox.domain.user.usecase.GetUserUseCase
import com.wodox.extension.setupNotification
import com.wodox.model.Constants
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject


@AndroidEntryPoint
class TaskDeadlineReceiver : BroadcastReceiver() {

    private companion object {
        const val TAG = "TaskDeadlineReceiver"
    }

    @Inject
    lateinit var getAllTasksByUserUseCase: GetAllTasksByUserUseCase

    @Inject
    lateinit var getUserUseCase: GetUserUseCase

    @Inject
    lateinit var settingsRepository: SettingsRepository

    override fun onReceive(context: Context, intent: Intent) {
        Log.d(TAG, "")
        Log.d(TAG, "╔═════════════════════════════════════════╗")
        Log.d(TAG, "║ 🔔 onReceive() CALLED                   ║")
        Log.d(TAG, "╚═════════════════════════════════════════╝")

        val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        val wakelock = powerManager.newWakeLock(
            PowerManager.PARTIAL_WAKE_LOCK,
            "wodox:TaskDeadlineReceiver"
        )

        Log.d(TAG, "   ⚡ Acquiring WakeLock (30s)...")
        wakelock.acquire(30000)  // 30 seconds
        Log.d(TAG, "   ✅ WakeLock acquired")

        ensureBackgroundThread {
            try {
                Log.d(TAG, "   3️⃣ Extracting ScheduleModel from Intent...")
                val scheduleModel =
                    intent.serializable<ScheduleModel>(Constants.Intents.TASK_DEADLINE)

                if (scheduleModel == null) {
                    Log.e(TAG, "   ❌ ScheduleModel is NULL!")
                    return@ensureBackgroundThread
                }

                Log.d(TAG, "   ✅ Got ScheduleModel: id=${scheduleModel.id}")

                Log.d(TAG, "   4️⃣ Calling repeatSchedule()...")
                repeatSchedule(context, scheduleModel)
                Log.d(TAG, "   ✅ repeatSchedule() completed")

            } catch (e: Exception) {
                Log.e(TAG, "   ❌ Error in onReceive", e)
            } finally {
                Log.d(TAG, "   5️⃣ Releasing WakeLock...")
                if (wakelock.isHeld) {
                    wakelock.release()
                    Log.d(TAG, "   ✅ WakeLock released")
                }
                Log.d(TAG, "")
            }
        }
    }

    private fun repeatSchedule(context: Context, scheduleModel: ScheduleModel) {
        Log.d(TAG, "   ┌─ repeatSchedule() called")

        CoroutineScope(Dispatchers.IO).launch {
            try {
                Log.d(TAG, "   │  6️⃣ Scheduling next alarm...")
                val nextTrigger = Calendar.getInstance().apply {
                    add(Calendar.MINUTE, 15)
                }
                val nextSchedule = scheduleModel.copy(triggerDate = nextTrigger)

                val sdf = SimpleDateFormat("HH:mm:ss dd/MM/yyyy", Locale.getDefault())
                Log.d(TAG, "   │     Next trigger: ${sdf.format(nextTrigger.time)}")

                context.setupNotification<TaskDeadlineReceiver>(
                    calendar = nextSchedule.triggerDate,
                    isRepeatable = false,
                    requestCode = nextSchedule.id,
                    Constants.Intents.TASK_DEADLINE to nextSchedule
                )
                Log.d(TAG, "   │  ✅ Next alarm scheduled")

                Log.d(TAG, "   │  7️⃣ Checking notification setting...")
                val isNotificationEnabled = settingsRepository.isNotificationEnabled.first()
                Log.d(TAG, "   │     isNotificationEnabled: $isNotificationEnabled")

                if (isNotificationEnabled) {
                    Log.d(TAG, "   │  8️⃣ Notification enabled → checking deadlines...")
                    handleCheckDeadline(context)
                } else {
                    Log.d(TAG, "   │  ⚠️ Notification disabled, SKIPPING deadline check")
                }

                Log.d(TAG, "   └─ repeatSchedule() completed")
            } catch (e: Exception) {
                Log.e(TAG, "   ❌ Error in repeatSchedule", e)
            }
        }
    }

    private suspend fun handleCheckDeadline(context: Context) {
        Log.d(TAG, "   ┌─ handleCheckDeadline() called")

        try {
            Log.d(TAG, "   │  Getting user...")
            val userId = getUserUseCase() ?: run {
                Log.e(TAG, "   │  ❌ User ID is NULL!")
                return
            }

            Log.d(TAG, "   │  ✅ User ID: $userId")

            Log.d(TAG, "   │  Getting all tasks...")
            val tasks = getAllTasksByUserUseCase(userId).first()
            Log.d(TAG, "   │  ✅ Got ${tasks.size} tasks")

            if (tasks.isEmpty()) {
                Log.d(TAG, "   │  ⚠️ No tasks found!")
                Log.d(TAG, "   └─ handleCheckDeadline() completed (no tasks)")
                return
            }

            Log.d(TAG, "   │  ─── Task Details ───")
            tasks.forEachIndexed { index, task ->
                val daysUntilDeadline = if (task.dueAt != null) {
                    val now = System.currentTimeMillis()
                    val dueTime = task.dueAt!!.time
                    val diffMs = dueTime - now
                    val days = java.util.concurrent.TimeUnit.MILLISECONDS.toDays(diffMs)
                    val hours = java.util.concurrent.TimeUnit.MILLISECONDS.toHours(diffMs) % 24
                    "$days ngày $hours giờ"
                } else {
                    "N/A"
                }

                Log.d(TAG, "   │  Task[$index]:")
                Log.d(TAG, "   │    • ID: ${task.id}")
                Log.d(TAG, "   │    • Title: ${task.title}")
                Log.d(TAG, "   │    • Due: ${task.dueAt}")
                Log.d(TAG, "   │    • Time until: $daysUntilDeadline")
                Log.d(TAG, "   │    • Status: ${task.status?.name}")
            }
            Log.d(TAG, "   │  ─── End Task Details ───")

            Log.d(TAG, "   │  Checking & notifying deadlines...")
            TaskNotificationManager.checkAndNotifyDeadlineTasks(context, tasks)
            Log.d(TAG, "   │  ✅ Deadline check completed")

            Log.d(TAG, "   └─ handleCheckDeadline() completed")
        } catch (e: Exception) {
            Log.e(TAG, "   ❌ Error in handleCheckDeadline", e)
            e.printStackTrace()
        }
    }
}