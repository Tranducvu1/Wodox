package com.wodox.ui.task.taskdetail.createtask

import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.wodox.domain.chat.model.local.NotificationActionType
import com.wodox.core.base.viewmodel.BaseUiStateViewModel
import com.wodox.core.extension.serializable
import com.wodox.domain.chat.model.local.Notification
import com.wodox.domain.chat.usecase.SaveNotificationUseCase
import com.wodox.domain.home.model.local.*
import com.wodox.domain.home.usecase.*
import com.wodox.domain.home.usecase.log.SaveLogUseCase
import com.wodox.domain.home.usecase.task.AnalyzeUserTasksUseCase
import com.wodox.domain.home.usecase.task.SaveTaskUseCase
import com.wodox.domain.home.usecase.taskassign.AssignUserToTaskUseCase
import com.wodox.domain.user.model.User
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
class CreateTaskViewModel @Inject constructor(
    val app: Application,
    private val saveSubTaskUseCase: SaveTaskUseCase,
    private val saveAttachmentUseCase: SaveAttachmentUseCase,
    private val getUserUseCase: GetUserUseCase,
    private val saveLogUseCase: SaveLogUseCase,
    private val saveTaskUseCase: SaveTaskUseCase,
    private val assignUserToTaskUseCase: AssignUserToTaskUseCase,
    private val getUserById: GetUserById,
    private val saveNotificationUseCase: SaveNotificationUseCase,
    private val getSuggestedSupportersUseCase: GetSuggestedSupportersUseCase,
    private val analyzeUserTasksUseCase: AnalyzeUserTasksUseCase,
) : BaseUiStateViewModel<CreateTaskUiState, CreateTaskUiEvent, CreateTaskUiAction>(app) {

    companion object {
        private const val TAG = "CreateTaskViewModel"
    }

    override fun initialState(): CreateTaskUiState = CreateTaskUiState()

    override fun onCreate() {
        super.onCreate()
        loadsTask()
    }

    val currentTask = MutableLiveData<Task>()
    val suggestedSupporters = MutableLiveData<List<User>>()
    val analysisResult = MutableLiveData<TaskAnalysisResult>()

    val task by lazy {
        data?.serializable<Task>(Constants.Intents.TASK)
    }

    override fun handleAction(action: CreateTaskUiAction) {
        super.handleAction(action)
        when (action) {
            is CreateTaskUiAction.DeleteAttachment -> deleteAttachment(action.attachment)
            is CreateTaskUiAction.SaveTask -> saveTask(action.task)
            is CreateTaskUiAction.AnalyzeUserSkill -> analyzeUserSkill()
            is CreateTaskUiAction.LoadSuggestedSupporters -> loadSuggestedSupporters(
                action.difficulty,
                action.priority
            )

            is CreateTaskUiAction.UpdateDifficulty -> updateDifficulty(
                action.difficulty,
                action.difficultyName
            )
        }
    }

    private fun loadsTask() {
        viewModelScope.launch {
            updateState {
                it.copy(
                    tasks = task
                )
            }
        }
    }

    private fun deleteAttachment(attachment: Attachment) {
        viewModelScope.launch(Dispatchers.IO) {
            attachment.deletedAt = Date()
            saveAttachmentUseCase(attachment)
            sendEvent(CreateTaskUiEvent.DeleteSuccess)
        }
    }

    private fun saveTask(task: Task) {
        viewModelScope.launch(Dispatchers.IO) {
            Log.d(TAG, "💾 Saving task: ${task.title}")

            val userId = getUserUseCase() ?: run {
                Log.e(TAG, "Cannot get userId")
                return@launch
            }

            val newTask = Task(
                id = UUID.randomUUID(),
                title = task.title,
                description = task.description,
                difficulty = task.difficulty,
                startAt = task.startAt,
                support = task.support,
                dueAt = task.dueAt,
                priority = task.priority,
                createdAt = Date(),
                updatedAt = Date(),
                ownerId = userId
            )

            saveSubTaskUseCase(newTask)

            currentTask.postValue(newTask)

            Log.d(TAG, "Task saved successfully: ${newTask.id}")

            val logTask = Log(
                id = UUID.randomUUID(),
                taskId = newTask.id,
                title = "You created this ${LogType.CREATED} ${newTask.title}",
                description = "Task created: ${newTask.description}",
                createdAt = Date()
            )
            saveLogUseCase(logTask)

            Log.d(TAG, "Starting auto-analysis after task creation...")
            analyzeUserSkill()

            sendEvent(CreateTaskUiEvent.SaveSuccess)
        }
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
                    Log.d(
                        TAG,
                        "📊 Avg Difficulty: ${String.format("%.1f", analysis.averageDifficulty)}"
                    )
                    Log.d(
                        TAG,
                        "⏳ Avg Completion Days: ${
                            String.format(
                                "%.1f",
                                analysis.averageCompletionDays
                            )
                        }"
                    )
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
                        sendEvent(CreateTaskUiEvent.AnalysisComplete(analysis))
                    }
                } else {
                    Log.w(TAG, "⚠️ Analysis returned null - no tasks found or error occurred")
                    updateState { it.copy(isAnalyzing = false) }
                }

            } catch (e: Exception) {
                Log.e(TAG, "❌ Error during analysis", e)
                updateState { it.copy(isAnalyzing = false) }

                withContext(Dispatchers.Main) {
                    sendEvent(CreateTaskUiEvent.Error("Analysis failed: ${e.message}"))
                }
            }

            Log.d(TAG, "═══════════════════════════════════════")
        }
    }

    private fun loadSuggestedSupporters(difficulty: Difficulty, priority: Priority) {
        viewModelScope.launch(Dispatchers.IO) {
            Log.d(TAG, "═══════════════════════════════════════")
            Log.d(TAG, "👥 Loading Suggested Supporters...")
            Log.d(TAG, "═══════════════════════════════════════")

            val userId = getUserUseCase() ?: run {
                Log.e(TAG, "❌ Cannot get userId")
                return@launch
            }

            Log.d(TAG, "📋 Task Requirements:")
            Log.d(TAG, "   • Difficulty: ${difficulty.displayName} (${difficulty.value})")
            Log.d(TAG, "   • Priority: ${priority.displayName} (${priority.value})")

            updateState { it.copy(isLoadingSupporters = true) }

            try {
                val supporters = getSuggestedSupportersUseCase(
                    SuggestedSupportersParams(
                        taskDifficulty = difficulty,
                        taskPriority = priority,
                        currentUserId = userId
                    )
                )

                Log.d(TAG, "✅ Found ${supporters.size} suitable supporters:")
                supporters.forEachIndexed { index, user ->
                    Log.d(TAG, "   ${index + 1}. ${user.name} - ${user.skillLevel.displayName}")
                }

                suggestedSupporters.postValue(supporters)
                updateState { it.copy(isLoadingSupporters = false) }

                withContext(Dispatchers.Main) {
                    sendEvent(CreateTaskUiEvent.SupportersLoaded(supporters))
                }

            } catch (e: Exception) {
                Log.e(TAG, "Error loading supporters", e)
                updateState { it.copy(isLoadingSupporters = false) }
            }

            Log.d(TAG, "═══════════════════════════════════════")
        }
    }

    private suspend fun saveLog(taskId: UUID, title: String, description: String?) {
        val log = Log(
            id = UUID.randomUUID(),
            taskId = taskId,
            title = title,
            description = description,
            createdAt = Date()
        )
        saveLogUseCase(log)
    }

    private fun updateDifficulty(difficulty: Int, difficultyName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val task = currentTask.value ?: return@launch

            val difficultyEnum =
                Difficulty.valueOf(difficultyName)

            val updatedTask = task.copy(difficulty = difficultyEnum)
            currentTask.postValue(updatedTask)
            saveTaskUseCase(updatedTask)

            saveLog(task.id, "Update difficulty", "Difficulty = $difficultyName")

            Log.d("TaskDetailVM", "✅ Difficulty updated to: $difficultyName")
        }
    }

}