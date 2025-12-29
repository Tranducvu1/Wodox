package com.wodox.data.home.repository

import android.util.Log
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.wodox.common.util.PromptUtils
import com.wodox.data.home.datasource.local.database.task.dao.AiChatDao
import com.wodox.data.home.datasource.local.database.task.dao.TaskDao
import com.wodox.data.home.datasource.local.database.task.mapper.AiChatMapper
import com.wodox.data.home.datasource.local.database.task.mapper.TaskMapper
import com.wodox.data.home.datasource.remote.datasource.AIChatDataSource
import com.wodox.domain.home.model.local.*
import com.wodox.domain.home.repository.TaskRepository
import com.wodox.domain.home.repository.UserFriendRepository
import com.wodox.domain.user.model.SkillLevel
import com.wodox.domain.user.model.User
import com.wodox.domain.user.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.util.Date
import java.util.UUID
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class TaskRepositoryImpl @Inject constructor(
    private val dao: TaskDao,
    private val mapper: TaskMapper,
    private val aiChatMapper: AiChatMapper,
    private val aiChatDao: AiChatDao,
    private val aiChatDataSource: AIChatDataSource,
    private val userRepository: UserRepository,
    private val userFriendRepository: UserFriendRepository,
    private val gson: Gson
) : TaskRepository {

    override suspend fun save(task: Task): Task? {
        return try {
            val entity = mapper.mapToEntity(task).apply {
                this.updatedAt = Date()
            }
            dao.save(entity)
            val savedTask = mapper.mapToDomain(entity)
            savedTask.copy(calculatedPriority = calculatePriority(savedTask))
        } catch (e: Exception) {
            Log.e("TaskRepository", "Error saving task: ${e.message}", e)
            null
        }
    }

    override suspend fun getTaskByTaskID(id: UUID): Task? {
        return try {
            val entity = dao.getTaskById(id)
            entity?.let {
                val task = mapper.mapToDomain(it)
                task.copy(calculatedPriority = calculatePriority(task))
            }
        } catch (e: Exception) {
            Log.e("TaskRepository", "Error getting task: ${e.message}", e)
            null
        }
    }

    override fun getTask(): Flow<List<Task>> {
        return dao.getAllTasks().map { entities ->
            val tasks = mapper.mapToDomainList(entities)
            tasks.map { task ->
                task.copy(calculatedPriority = calculatePriority(task))
            }.sortedByDescending { it.calculatedPriority }
        }
    }


    override fun getTaskCalendar(): Flow<List<Task>> {
        return dao.getAllTasks().map { entities ->
            mapper.mapToDomainList(entities)
        }
    }

    override suspend fun saveChatHistory(
        userMessage: String,
        aiResponse: String,
        taskId: String?
    ): AiChat? {
        return try {
            val chat = AiChat(
                taskId = taskId,
                userMessage = userMessage,
                aiResponse = aiResponse
            )
            val entity = aiChatMapper.mapToEntity(chat)
            aiChatDao.insert(entity)
            chat
        } catch (e: Exception) {
            Log.e("TaskRepository", "Error saving chat history: ${e.message}", e)
            null
        }
    }

    override fun getAllTaskByUserID(ownerId: UUID): Flow<PagingData<Task>> {
        Log.d("TaskRepository", "🔵 getAllTaskByUserID called for ownerId: $ownerId")

        return Pager(
            config = PagingConfig(
                pageSize = 20,
                prefetchDistance = 2,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { dao.getTasksForUser(ownerId) }
        ).flow.map { pagingData ->
            Log.d("TaskRepository", "📦 Received pagingData from DAO")

            pagingData.map { entity ->
                val task = mapper.mapToDomain(entity)
                val taskWithPriority = task.copy(calculatedPriority = calculatePriority(task))

                Log.d("TaskRepository",
                    "📄 Task: '${taskWithPriority.title}' | Priority: %.2f".format(taskWithPriority.calculatedPriority)
                )

                taskWithPriority
            }.also { processedData ->
                Log.d("TaskRepository", "✅ All tasks processed with priority calculated")
            }
        }
    }

    override fun getAllTaskFavourite(ownerId: UUID): Flow<PagingData<Task>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                prefetchDistance = 2,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { dao.getTaskFavouritePaging(ownerId) }
        ).flow.map { pagingData ->
            pagingData.map { entity ->
                val task = mapper.mapToDomain(entity)
                task.copy(calculatedPriority = calculatePriority(task))
            }
        }
    }

    override suspend fun askAI(input: String): String? {
        return try {
            val systemPrompt = PromptUtils.getPrompt(input)
            aiChatDataSource.simpleAiChat(
                input = input,
                systemPrompt = systemPrompt
            )
        } catch (e: Exception) {
            Log.e("TaskRepository", "Error asking AI: ${e.message}", e)
            null
        }
    }

    override suspend fun analyzeUserTasks(userId: UUID): TaskAnalysisResult? {
        return try {
            Log.d("TaskRepository", "🔍 Starting analysis for user: $userId")

            val allTaskEntities = dao.getAllTasksByUserId(userId)
            val allTasks = allTaskEntities.map { mapper.mapToDomain(it) }

            if (allTasks.isEmpty()) {
                Log.w("TaskRepository", "⚠️ No tasks found for user")
                return null
            }

            Log.d("TaskRepository", "📊 Found ${allTasks.size} tasks")

            val stats = calculateTaskStatistics(allTasks)
            Log.d(
                "TaskRepository", """
                📈 Statistics:
                   Completion Rate: ${String.format("%.1f", stats.completionRate)}%
                   On-Time Rate: ${String.format("%.1f", stats.onTimeRate)}%
                   Avg Priority: ${String.format("%.1f", stats.avgPriority)}
                   Avg Difficulty: ${String.format("%.1f", stats.avgDifficulty)}
            """.trimIndent()
            )

            val analysisPrompt = PromptUtils.getTaskAnalysisPrompt(
                totalTasks = stats.totalTasks,
                completedTasks = stats.completedTasks,
                onTimeTasks = stats.onTimeTasks,
                lateTasks = stats.lateTasks,
                avgPriority = stats.avgPriority,
                avgDifficulty = stats.avgDifficulty,
                avgCompletionDays = stats.avgCompletionDays,
                completionRate = stats.completionRate,
                onTimeRate = stats.onTimeRate
            )

            Log.d("TaskRepository", "💬 Sending analysis request to AI...")

            val aiResponse = aiChatDataSource.simpleAiChat(
                input = analysisPrompt,
                systemPrompt = null
            )

            if (aiResponse.isNullOrBlank()) {
                Log.e("TaskRepository", "❌ AI returned empty response")
                return createFallbackAnalysis(userId, stats)
            }

            Log.d("TaskRepository", "🤖 AI Response received: ${aiResponse.take(200)}...")

            val aiResult = parseAIResponse(aiResponse)
            Log.d(
                "TaskRepository",
                "✅ Parsed: score=${aiResult.skillScore}, level=${aiResult.skillLevel}"
            )

            val result = TaskAnalysisResult(
                userId = userId,
                totalTasks = stats.totalTasks,
                completedTasks = stats.completedTasks,
                onTimeTasks = stats.onTimeTasks,
                lateTasks = stats.lateTasks,
                averagePriority = stats.avgPriority,
                averageDifficulty = stats.avgDifficulty,
                averageCompletionDays = stats.avgCompletionDays,
                skillScore = aiResult.skillScore.coerceIn(0.0, 10.0),
                suggestedLevel = try {
                    SkillLevel.valueOf(aiResult.skillLevel.uppercase())
                } catch (e: Exception) {
                    Log.w(
                        "TaskRepository",
                        "Invalid skill level: ${aiResult.skillLevel}, using score-based"
                    )
                    SkillLevel.fromValue(aiResult.skillScore)
                },
                analyzedAt = Date(),
                insights = aiResult.insights
            )

            Log.d(
                "TaskRepository",
                "🎯 Analysis complete: ${result.suggestedLevel.displayName} (${result.skillScore})"
            )
            result

        } catch (e: Exception) {
            Log.e("TaskRepository", "❌ Error analyzing tasks", e)
            null
        }
    }

    override suspend fun getSuggestedSupporters(
        taskDifficulty: Difficulty,
        taskPriority: Priority,
        currentUserId: UUID
    ): List<User> {
        return try {
            Log.d("TaskRepository", "═══════════════════════════════════════")
            Log.d("TaskRepository", "🔍 Getting suggested supporters...")
            Log.d("TaskRepository", "═══════════════════════════════════════")
            Log.d("TaskRepository", "📋 Task Requirements:")
            Log.d(
                "TaskRepository",
                "   • Difficulty: ${taskDifficulty.displayName} (${taskDifficulty.value})"
            )
            Log.d(
                "TaskRepository",
                "   • Priority: ${taskPriority.displayName} (${taskPriority.value})"
            )

            val acceptedFriends = userFriendRepository.getAcceptedFriends(currentUserId)
                .first()

            Log.d("TaskRepository", "👥 Found ${acceptedFriends.size} accepted friends")

            if (acceptedFriends.isEmpty()) {
                Log.w("TaskRepository", "⚠️ No friends found")
                Log.d("TaskRepository", "═══════════════════════════════════════")
                return emptyList()
            }

            val friendUsers = acceptedFriends.mapNotNull { friendRelation ->
                val friendId = if (friendRelation.userId == currentUserId) {
                    friendRelation.friendId
                } else {
                    friendRelation.userId
                }
                val user = userRepository.getUserById(friendId)
                if (user != null) {
                    Log.d(
                        "TaskRepository",
                        "   ✓ Found friend: ${user.name} (${user.skillLevel.displayName})"
                    )
                } else {
                    Log.w("TaskRepository", "   ✗ Could not load user: $friendId")
                }
                user
            }

            Log.d("TaskRepository", "📊 Total friend users loaded: ${friendUsers.size}")

            val requiredLevel = calculateRequiredSkillLevel(taskDifficulty, taskPriority)
            Log.d("TaskRepository", "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
            Log.d("TaskRepository", "🎯 Required Skill Level: ${requiredLevel.displayName}")
            Log.d("TaskRepository", "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")

            val suitableSupporters = friendUsers
                .filter { user ->
                    val isQualified = user.skillLevel >= requiredLevel
                    Log.d(
                        "TaskRepository",
                        "   ${if (isQualified) "✅" else "❌"} ${user.name}: ${user.skillLevel.displayName} ${if (isQualified) ">=" else "<"} ${requiredLevel.displayName}"
                    )
                    isQualified
                }
                .sortedByDescending { it.skillLevel }

            Log.d("TaskRepository", "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
            Log.d(
                "TaskRepository",
                "✅ Final Result: ${suitableSupporters.size} suitable supporters"
            )
            if (suitableSupporters.isNotEmpty()) {
                suitableSupporters.forEachIndexed { index, user ->
                    Log.d(
                        "TaskRepository",
                        "   ${index + 1}. ${user.name} - ${user.skillLevel.displayName}"
                    )
                }
            } else {
                Log.d("TaskRepository", "   No supporters meet the required skill level")
            }
            Log.d("TaskRepository", "═══════════════════════════════════════")

            suitableSupporters

        } catch (e: Exception) {
            Log.e("TaskRepository", "❌ Error getting supporters", e)
            Log.d("TaskRepository", "═══════════════════════════════════════")
            emptyList()
        }
    }

    override fun getChatsByTaskId(taskId: String): Flow<List<AiChat>> {
        return aiChatDao.getChatsByTaskId(taskId).map { entities ->
            aiChatMapper.mapToDomainList(entities)
        }
    }

    override fun getRecentChats(limit: Int): Flow<List<AiChat>> {
        return aiChatDao.getRecentChats(limit).map { entities ->
            aiChatMapper.mapToDomainList(entities)
        }
    }

    override suspend fun deleteChatHistory(chatId: String) {
        try {
            aiChatDao.getChatById(chatId)?.let {
                aiChatDao.delete(it)
            }
        } catch (e: Exception) {
            Log.e("TaskRepository", "Error deleting chat: ${e.message}", e)
        }
    }

    override suspend fun deleteChatsByTaskId(taskId: String) {
        try {
            aiChatDao.deleteByTaskId(taskId)
        } catch (e: Exception) {
            Log.e("TaskRepository", "Error deleting chats by task: ${e.message}", e)
        }
    }

    private fun calculateTaskStatistics(tasks: List<Task>): TaskStatistics {
        val completedTasks = tasks.filter { it.status == TaskStatus.DONE }

        val onTimeTasks = completedTasks.count { task ->
            val dueDate = task.dueAt ?: return@count true
            task.updatedAt <= dueDate
        }

        val avgPriority = tasks.map { it.priority.value }.average()
        val avgDifficulty = tasks.map { it.difficulty.value }.average()

        val avgCompletionDays = completedTasks
            .mapNotNull { task ->
                val start = task.startAt ?: task.createdAt
                val days = TimeUnit.MILLISECONDS.toDays(task.updatedAt.time - start.time)
                days.toDouble()
            }
            .average()
            .takeIf { !it.isNaN() } ?: 0.0

        val completionRate = if (tasks.isNotEmpty()) {
            (completedTasks.size.toDouble() / tasks.size) * 100
        } else 0.0

        val onTimeRate = if (completedTasks.isNotEmpty()) {
            (onTimeTasks.toDouble() / completedTasks.size) * 100
        } else 0.0

        return TaskStatistics(
            totalTasks = tasks.size,
            completedTasks = completedTasks.size,
            onTimeTasks = onTimeTasks,
            lateTasks = completedTasks.size - onTimeTasks,
            avgPriority = avgPriority,
            avgDifficulty = avgDifficulty,
            avgCompletionDays = avgCompletionDays,
            completionRate = completionRate,
            onTimeRate = onTimeRate
        )
    }

    private fun parseAIResponse(aiResponse: String?): AIAnalysisResponse {
        return try {
            if (aiResponse.isNullOrBlank()) {
                throw Exception("Empty response")
            }

            // Clean response
            val cleanJson = aiResponse
                .replace("```json", "")
                .replace("```", "")
                .trim()
                .let { json ->
                    // Find JSON object
                    val start = json.indexOf('{')
                    val end = json.lastIndexOf('}')
                    if (start != -1 && end != -1 && end > start) {
                        json.substring(start, end + 1)
                    } else {
                        json
                    }
                }

            Log.d("TaskRepository", "🧹 Clean JSON: $cleanJson")

            val result = gson.fromJson(cleanJson, AIAnalysisResponse::class.java)

            require(result.skillScore in 0.0..10.0) { "Invalid score: ${result.skillScore}" }
            require(result.insights.isNotEmpty()) { "No insights" }

            result

        } catch (e: JsonSyntaxException) {
            Log.e("TaskRepository", "❌ JSON parse error", e)
            createFallbackAIResponse()
        } catch (e: Exception) {
            Log.e("TaskRepository", "❌ Parse error: ${e.message}", e)
            createFallbackAIResponse()
        }
    }

    private fun createFallbackAIResponse(): AIAnalysisResponse {
        return AIAnalysisResponse(
            skillScore = 5.0,
            skillLevel = "JUNIOR",
            insights = listOf(
                "Analysis temporarily unavailable",
                "Default assessment applied",
                "Complete more tasks for accurate evaluation"
            )
        )
    }

    private fun createFallbackAnalysis(userId: UUID, stats: TaskStatistics): TaskAnalysisResult {
        val score = (stats.completionRate / 100 * 3) +
                (stats.onTimeRate / 100 * 3) +
                (stats.avgDifficulty / 10 * 2) +
                (stats.avgPriority / 10 * 2)

        return TaskAnalysisResult(
            userId = userId,
            totalTasks = stats.totalTasks,
            completedTasks = stats.completedTasks,
            onTimeTasks = stats.onTimeTasks,
            lateTasks = stats.lateTasks,
            averagePriority = stats.avgPriority,
            averageDifficulty = stats.avgDifficulty,
            averageCompletionDays = stats.avgCompletionDays,
            skillScore = score.coerceIn(0.0, 10.0),
            suggestedLevel = SkillLevel.fromScore(score),
            analyzedAt = Date(),
            insights = listOf(
                "Completion: ${String.format("%.1f", stats.completionRate)}%",
                "On-time: ${String.format("%.1f", stats.onTimeRate)}%",
                "Avg difficulty: ${String.format("%.1f", stats.avgDifficulty)}/10"
            )
        )
    }

    private fun calculateRequiredSkillLevel(
        difficulty: Difficulty,
        priority: Priority
    ): SkillLevel {
        val score = (difficulty.value * 0.6) + (priority.value * 0.4)

        return when {
            score >= 9.0 -> SkillLevel.EXPERT
            score >= 7.5 -> SkillLevel.SENIOR
            score >= 5.5 -> SkillLevel.MEDIUM
            score >= 3.5 -> SkillLevel.JUNIOR
            score >= 2.0 -> SkillLevel.FRESHER
            else -> SkillLevel.INTERN
        }
    }

    private fun calculatePriority(task: Task): Double {
        val tag = "PriorityCalc"

        // 1️⃣ User priority
        val userPriorityScore = task.priority.value * 0.30
        Log.d(tag, "UserPriority: ${task.priority.value} * 0.30 = $userPriorityScore")

        // 2️⃣ Deadline
        val deadlineScore = task.dueAt?.let { dueDate ->
            val diffMillis = dueDate.time - System.currentTimeMillis()
            val daysLeft = TimeUnit.MILLISECONDS.toDays(diffMillis).toInt()

            val score = when {
                daysLeft < 0 -> 10.0
                daysLeft == 0 -> 9.5
                daysLeft == 1 -> 8.5
                daysLeft <= 2 -> 7.5
                daysLeft <= 3 -> 7.0
                daysLeft <= 5 -> 5.0
                daysLeft <= 7 -> 4.0
                daysLeft <= 14 -> 3.0
                else -> 1.5
            }

            Log.d(
                tag,
                "Deadline: dueAt=${dueDate.time}, daysLeft=$daysLeft → score=$score"
            )

            score
        } ?: run {
            Log.d(tag, "Deadline: no due date → score=0.0")
            0.0
        }

        val deadlineWeighted = deadlineScore * 0.40
        Log.d(tag, "DeadlineWeighted: $deadlineScore * 0.40 = $deadlineWeighted")

        // 3️⃣ Difficulty
        val difficultyScore = task.difficulty.value * 0.15
        Log.d(tag, "Difficulty: ${task.difficulty.value} * 0.15 = $difficultyScore")

        // 4️⃣ Dependency
        val rawDependencyScore = calculateDependencyScore(task)
        val dependencyScore = rawDependencyScore * 0.15
        Log.d(tag, "Dependency: $rawDependencyScore * 0.15 = $dependencyScore")

        // 5️⃣ Total
        val total =
            userPriorityScore + deadlineWeighted + difficultyScore + dependencyScore

        Log.d(
            tag,
            """
        ===== PRIORITY RESULT =====
        TaskId: ${task.id}
        UserPriority: $userPriorityScore
        Deadline: $deadlineWeighted
        Difficulty: $difficultyScore
        Dependency: $dependencyScore
        ---------------------------
        TOTAL SCORE = $total
        ===========================
        """.trimIndent()
        )

        return total
    }


    private fun calculateDependencyScore(task: Task): Double {
        var score = 5.0
        score -= (task.assignedUserIds.size - 1).coerceAtLeast(0) * 0.5
        return score.coerceIn(0.0, 10.0)
    }
}
