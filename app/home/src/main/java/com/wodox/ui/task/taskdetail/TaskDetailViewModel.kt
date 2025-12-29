package com.wodox.ui.task.taskdetail

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.wodox.chat.model.local.NotificationActionType
import com.wodox.core.base.viewmodel.BaseUiStateViewModel
import com.wodox.core.extension.serializable
import com.wodox.domain.chat.model.local.Notification
import com.wodox.domain.chat.usecase.SaveNotificationUseCase
import com.wodox.domain.home.model.local.Attachment
import com.wodox.domain.home.model.local.AttachmentType
import com.wodox.domain.home.model.local.Priority
import com.wodox.domain.home.model.local.SubTask
import com.wodox.domain.home.model.local.Task
import com.wodox.domain.home.model.local.TaskAnalysisResult
import com.wodox.domain.home.model.local.TaskAssignee
import com.wodox.domain.home.model.local.TaskStatus
import com.wodox.domain.home.usecase.AnalyzeUserTasksUseCase
import com.wodox.domain.home.usecase.GetAttachmentUseCase
import com.wodox.domain.home.usecase.SaveAttachmentUseCase
import com.wodox.domain.home.usecase.SaveLogUseCase
import com.wodox.domain.home.usecase.SaveTaskUseCase
import com.wodox.domain.home.usecase.subtask.GetAllSubTaskByTaskIdUseCase
import com.wodox.domain.home.usecase.subtask.SaveSubTaskUseCase
import com.wodox.domain.home.usecase.taskassign.AssignUserToTaskUseCase
import com.wodox.domain.home.usecase.taskassign.GetTaskAssignByTaskId
import com.wodox.domain.user.usecase.GetUserById
import com.wodox.domain.user.usecase.GetUserUseCase
import com.wodox.model.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Date
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class TaskDetailViewModel @Inject constructor(
    val app: Application,
    private val saveAttachmentUseCase: SaveAttachmentUseCase,
    private val saveTaskUseCase: SaveTaskUseCase,
    private val getAttachmentUseCase: GetAttachmentUseCase,
    private val getAllSubTaskByTaskIdUseCase: GetAllSubTaskByTaskIdUseCase,
    private val saveSubTaskUseCase: SaveSubTaskUseCase,
    private val assignUserToTaskUseCase: AssignUserToTaskUseCase,
    private val getTaskAssignByTaskId: GetTaskAssignByTaskId,
    private val getUserById: GetUserById,
    private val getUserUseCase: GetUserUseCase,
    private val saveNotificationUseCase: SaveNotificationUseCase,
    private val saveLogUseCase: SaveLogUseCase,
    private val analyzeUserTasksUseCase: AnalyzeUserTasksUseCase,
    ) : BaseUiStateViewModel<TaskDetailUiState, TaskDetailUiEvent, TaskDetailUiAction>(app) {

    companion object {
        private const val TAG = "TaskDetailViewModel"
    }

    override fun initialState(): TaskDetailUiState = TaskDetailUiState()

    val currentTask = MutableLiveData<Task>()

    val analysisResult = MutableLiveData<TaskAnalysisResult>()


    val task by lazy {
        data?.serializable<Task>(Constants.Intents.TASK)
    }

    override fun onCreate() {
        super.onCreate()
        Log.d("TaskDetailVM", "task = $task")
        Log.d("TaskDetailVM", "taskId = ${task?.id}")
        currentTask.value = task
        loadUserAssign()
        loadSubTask()
        loadAttachment()
    }

    override fun handleAction(action: TaskDetailUiAction) {
        super.handleAction(action)
        when (action) {
            is TaskDetailUiAction.UpdateTaskState -> updateTaskStatus(action.status)
            is TaskDetailUiAction.UpdateDay -> updateDate(action.start, action.end)
            is TaskDetailUiAction.UpdateAttachment ->
                saveAttachment(action.uri, action.type, action.taskID)

            is TaskDetailUiAction.UpdatePriority -> updatePriority(action.priority)
            is TaskDetailUiAction.LoadAttachment -> loadAttachment()
            is TaskDetailUiAction.DeleteAttachment -> deleteAttachment(action.attachment)
            is TaskDetailUiAction.UpdateTitle -> handleUpdateTitle(action.title)
            is TaskDetailUiAction.DeleteSubTask -> deleteSubTask(action.subTask)
            is TaskDetailUiAction.AssignUser -> assignUserToTask(action.id)
            TaskDetailUiAction.AssignSuccessfully -> loadUserAssign()
            TaskDetailUiAction.AnalyzeUserSkill -> analyzeUserSkill()
            is TaskDetailUiAction.UpdateDifficulty -> updateDifficulty(action.difficulty,action.difficultyName)
        }
    }


    private fun loadUserAssign() {
        viewModelScope.launch(Dispatchers.IO) {
            val taskId = currentTask.value?.id ?: return@launch

            updateState { it.copy(isLoading = true) }

            val taskAssign = getTaskAssignByTaskId(taskId)
            val userAssign = taskAssign?.let { getUserById(it.userId) }
            val userId = getUserUseCase() ?: return@launch

            updateState {
                it.copy(
                    user = userAssign,
                    currentUserId = userId,
                    isLoading = false
                )
            }
        }
    }

    private fun loadSubTask() {
        val taskId = currentTask.value?.id ?: return
        viewModelScope.launch(Dispatchers.IO) {
            getAllSubTaskByTaskIdUseCase(taskId).collect { subtasks ->
                updateState { it.copy(subTasks = subtasks) }
            }
        }
    }

    private fun loadAttachment() {
        val taskId = currentTask.value?.id ?: return
        viewModelScope.launch {
            getAttachmentUseCase(taskId).collect { attachments ->
                updateState { it.copy(attachments = attachments) }
            }
        }
    }


    private fun updateTaskStatus(status: TaskStatus) {
        viewModelScope.launch(Dispatchers.IO) {
            val task = currentTask.value ?: return@launch
            val updatedTask = task.copy(status = status)
            currentTask.postValue(updatedTask)
            saveTaskUseCase(updatedTask)

            saveLog(task.id, "Update status", "Status = $status")
        }
    }

    private fun updateDate(start: Long, end: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val task = currentTask.value ?: return@launch
            val updatedTask = task.copy(
                startAt = Date(start),
                dueAt = Date(end)
            )
            currentTask.postValue(updatedTask)
            saveTaskUseCase(updatedTask)
            saveLog(task.id, "Update date", "Updated start/due date")
        }
    }

    private fun updatePriority(priority: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val task = currentTask.value ?: return@launch
            val priorityEnum = Priority.fromValue(priority)

            val updatedTask = task.copy(priority = priorityEnum)
            currentTask.postValue(updatedTask)
            saveTaskUseCase(updatedTask)

            saveLog(task.id, "Update priority", "Priority = $priority")
        }
    }

    private fun handleUpdateTitle(title: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val task = currentTask.value ?: return@launch
            val updatedTask = task.copy(title = title)
            currentTask.postValue(updatedTask)
            saveTaskUseCase(updatedTask)

            saveLog(task.id, "Update title", title)

            withContext(Dispatchers.Main) {
                sendEvent(TaskDetailUiEvent.UpdateSuccess)
            }
        }
    }


    private fun saveAttachment(uri: Uri, type: AttachmentType, taskID: UUID?) {
        viewModelScope.launch(Dispatchers.IO) {
            val task = currentTask.value ?: return@launch
            val attachment = Attachment(
                id = UUID.randomUUID(),
                taskId = taskID ?: task.id, // 🔥 FIX
                uri = uri.toString(),
                type = type,
                name = extractFileName(uri)
            )
            saveAttachmentUseCase(attachment)

            saveLog(task.id, "Add attachment", type.name)
        }
    }

    private fun deleteAttachment(attachment: Attachment) {
        viewModelScope.launch(Dispatchers.IO) {
            attachment.deletedAt = Date()
            saveAttachmentUseCase(attachment)

            attachment.taskId?.let {
                saveLog(it, "Delete attachment", attachment.name)
            }

            sendEvent(TaskDetailUiEvent.DeleteSuccess)
        }
    }


    private fun deleteSubTask(subtask: SubTask) {
        viewModelScope.launch(Dispatchers.IO) {
            saveSubTaskUseCase(subtask.copy(deletedAt = Date()))

            subtask.taskId?.let {
                saveLog(it, "Delete subtask", subtask.title)
            }
        }
    }
    private fun assignUserToTask(userId: UUID) {
        viewModelScope.launch(Dispatchers.IO) {
            val task = currentTask.value ?: return@launch
            val ownerId = uiState.value.currentUserId ?: return@launch

            val assign = TaskAssignee(
                id = UUID.randomUUID(),
                ownerId = ownerId,
                taskId = task.id,
                userId = userId,
                assignedAt = Date(),
            )
            assignUserToTaskUseCase(assign)

            val updatedTask = task.copy(
                assignedUserIds = (task.assignedUserIds + userId).distinct()
            )
            currentTask.postValue(updatedTask)
            saveTaskUseCase(updatedTask)

            val fromUser = getUserById(ownerId)

            val notification = Notification(
                id = UUID.randomUUID(),
                userId = userId,
                fromUserId = ownerId,
                fromUserName = fromUser?.name ?: "Someone",
                userAvatar = fromUser?.avatar ?: "",
                taskId = task.id,
                taskName = updatedTask.title,
                actionType = NotificationActionType.ASSIGNED,
                content = "${fromUser?.name ?: "Someone"} assigned you a task",
                timestamp = System.currentTimeMillis(),
                updatedAt = Date(),
                createdAt = Date(),
                dismissedAt = null,
                readAt = null,
                deletedAt = null,
                isRead = false,
                isDismissed = false
            )

            saveNotificationUseCase(notification)

            saveLog(task.id, "Assign user", "userId = $userId")

            withContext(Dispatchers.Main) {
                sendEvent(TaskDetailUiEvent.AssignSuccess)
            }
        }
    }


    private suspend fun saveLog(taskId: UUID, title: String, description: String?) {
        val log = com.wodox.domain.home.model.local.Log(
            id = UUID.randomUUID(),
            taskId = taskId,
            title = title,
            description = description,
            createdAt = Date()
        )
        saveLogUseCase(log)
    }

    private fun extractFileName(uri: Uri): String {
        val path = uri.path ?: return ""
        return path.substringAfterLast("/").substringBefore(".")
    }

    private fun analyzeUserSkill() {
        viewModelScope.launch(Dispatchers.IO) {
            Log.d(TAG, "═══════════════════════════════════════")
            Log.d(TAG, "🤖 Starting AI Task Analysis...")
            Log.d(TAG, "═══════════════════════════════════════")

            val userId = getUserUseCase() ?: run {
                Log.e(TAG, "❌ Cannot get userId for analysis")
                return@launch
            }

            Log.d(TAG, "👤 User ID: $userId")
            updateState { it.copy(isAnalyzing = true) }

            try {
                Log.d(TAG, "📡 Calling analyzeUserTasksUseCase...")
                val analysis = analyzeUserTasksUseCase(userId)

                if (analysis != null) {
                    Log.d(TAG, "✅ Analysis completed successfully!")
                    Log.d(TAG, "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
                    Log.d(TAG, "📊 ANALYSIS RESULTS:")
                    Log.d(TAG, "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
                    Log.d(TAG, "📈 Total Tasks: ${analysis.totalTasks}")
                    Log.d(TAG, "✅ Completed: ${analysis.completedTasks}")
                    Log.d(TAG, "⏰ On-Time: ${analysis.onTimeTasks}")
                    Log.d(TAG, "⏱️ Late: ${analysis.lateTasks}")
                    Log.d(TAG, "📊 Avg Priority: ${String.format("%.1f", analysis.averagePriority)}")
                    Log.d(TAG, "📊 Avg Difficulty: ${String.format("%.1f", analysis.averageDifficulty)}")
                    Log.d(TAG, "⏳ Avg Completion Days: ${String.format("%.1f", analysis.averageCompletionDays)}")
                    Log.d(TAG, "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
                    Log.d(TAG, "🎯 SKILL ASSESSMENT:")
                    Log.d(TAG, "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
                    Log.d(TAG, "⭐ Skill Score: ${String.format("%.2f", analysis.skillScore)}/10")
                    Log.d(TAG, "🏆 Skill Level: ${analysis.suggestedLevel.displayName}")
                    Log.d(TAG, "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
                    Log.d(TAG, "💡 INSIGHTS:")
                    analysis.insights.forEachIndexed { index, insight ->
                        Log.d(TAG, "   ${index + 1}. $insight")
                    }
                    Log.d(TAG, "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")

                    analysisResult.postValue(analysis)
                    updateState { state ->
                        state.copy(
                            userSkillAnalysis = analysis,
                            isAnalyzing = false
                        )
                    }

                    withContext(Dispatchers.Main) {
                        sendEvent(TaskDetailUiEvent.AnalysisComplete(analysis))
                    }
                } else {
                    Log.w(TAG, "⚠️ Analysis returned null - no tasks found or error occurred")
                    updateState { it.copy(isAnalyzing = false) }
                }

            } catch (e: Exception) {
                Log.e(TAG, "❌ Error during analysis", e)
                updateState { it.copy(isAnalyzing = false) }

                withContext(Dispatchers.Main) {
                    sendEvent(TaskDetailUiEvent.Error("Analysis failed: ${e.message}"))
                }
            }

            Log.d(TAG, "═══════════════════════════════════════")
        }
    }

    private fun updateDifficulty(difficulty: Int, difficultyName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val task = currentTask.value ?: return@launch

            val difficultyEnum = com.wodox.domain.home.model.local.Difficulty.valueOf(difficultyName)

            val updatedTask = task.copy(difficulty = difficultyEnum)
            currentTask.postValue(updatedTask)
            saveTaskUseCase(updatedTask)

            saveLog(task.id, "Update difficulty", "Difficulty = $difficultyName")

            Log.d("TaskDetailVM", "✅ Difficulty updated to: $difficultyName")
        }
    }

}
