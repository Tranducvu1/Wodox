package com.wodox.domain.home.repository

import androidx.paging.PagingData
import com.wodox.domain.home.model.local.AiChat
import com.wodox.domain.home.model.local.Difficulty
import com.wodox.domain.home.model.local.Priority
import com.wodox.domain.home.model.local.Task
import com.wodox.domain.home.model.local.TaskAnalysisResult
import com.wodox.domain.home.model.local.TaskStatus
import com.wodox.domain.user.model.User
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.util.UUID

interface TaskRepository {

    suspend fun save(task: Task): Task?
    suspend fun getTaskByTaskID(id: UUID): Task?
    fun getTask(): Flow<List<Task>>
    fun getTaskCalendar(): Flow<List<Task>>
    suspend fun saveChatHistory(
        userMessage: String,
        aiResponse: String,
        taskId: String? = null
    ): AiChat?

    suspend fun getAllTasksByUserId(userId: UUID): Flow<List<Task>>
    fun getAllTaskByUserID(ownerId: UUID): Flow<PagingData<Task>>
    fun getAllTaskFavourite(ownerId: UUID): Flow<PagingData<Task>>
    suspend fun askAI(input: String): String?
    suspend fun analyzeUserTasks(userId: UUID): TaskAnalysisResult?
    suspend fun getSuggestedSupporters(
        taskDifficulty: Difficulty,
        taskPriority: Priority,
        currentUserId: UUID
    ): List<User>

    fun getChatsByTaskId(taskId: String): Flow<List<AiChat>>
    fun getRecentChats(limit: Int): Flow<List<AiChat>>
    suspend fun deleteChatHistory(chatId: String)
    suspend fun deleteChatsByTaskId(taskId: String)

    fun getTasksByStatus(status: TaskStatus): Flow<List<Task>>
    fun getCompletedTasks(): Flow<List<Task>>
    fun getPendingTasks(): Flow<List<Task>>
    fun getOverdueTasks(): Flow<List<Task>>
    fun getTasksByPriority(priority: Priority): Flow<List<Task>>
    fun getTasksSortedByDate(): Flow<List<Task>>
    fun getTasksSortedByPriority(): Flow<List<Task>>
    fun getTasksSortedByName(): Flow<List<Task>>

}
