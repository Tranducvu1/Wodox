package com.wodox.common.util

import com.wodox.domain.home.model.local.Task
import com.wodox.domain.home.model.local.TaskStatus
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.TimeUnit

object PromptUtils {

    private val workKeywords = listOf(
        "task", "work", "job", "project", "todo",
        "assignment", "deadline", "due", "plan"
    )

    fun getPrompt(input: String): String {
        val lowerInput = input.lowercase()

        val isWorkRelated = workKeywords.any { keyword ->
            lowerInput.contains(keyword)
        }

        return if (isWorkRelated) {
            """
You are a professional AI task management assistant. Analyze the following task or work-related information in a clear, detailed manner and provide the following:

1. A concise but detailed summary including:
   - Task urgency level and priority assessment
   - Current status and progress
   - Key dates (creation date, start date, due date if available)
   - Important tags or categories
   - Assignment details
   - Any attachments or additional information

2. Generate **creative ideas or approaches** that could improve or accelerate the task. Suggest alternative ways to achieve goals if applicable.

3. Provide **practical solutions or actionable next steps** to resolve any issues or move the task forward efficiently.

Write your response as a **natural, flowing paragraph** for the summary, and then list ideas and solutions clearly. Be specific about dates using full formats (e.g., "November 3, 2025"). Use professional, clear, and concise language.

Task input:
$input
        """.trimIndent()
        } else {
            """
You are a helpful AI assistant. Respond naturally and helpfully to the following message:

$input
        """.trimIndent()
        }
    }


    fun getTaskAnalysisPrompt(
        totalTasks: Int,
        completedTasks: Int,
        onTimeTasks: Int,
        lateTasks: Int,
        avgPriority: Double,
        avgDifficulty: Double,
        avgCompletionDays: Double,
        completionRate: Double,
        onTimeRate: Double
    ): String {
        return """
Analyze this user's task performance data and evaluate their skill level.

📊 TASK STATISTICS:
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
Total Tasks: $totalTasks
Completed: $completedTasks (${String.format("%.1f", completionRate)}%)
On-Time Delivery: $onTimeTasks (${String.format("%.1f", onTimeRate)}%)
Late Completion: $lateTasks

📈 PERFORMANCE METRICS:
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
Average Priority Handled: ${String.format("%.1f", avgPriority)}/10
Average Difficulty Handled: ${String.format("%.1f", avgDifficulty)}/10
Average Completion Time: ${String.format("%.1f", avgCompletionDays)} days

🎯 YOUR TASK:
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
Based on these metrics, evaluate the user's skill level using these criteria:

1. COMPLETION RATE (30% weight):
   - 90-100%: Excellent
   - 75-89%: Good
   - 60-74%: Average
   - <60%: Needs improvement

2. ON-TIME DELIVERY (30% weight):
   - 90-100%: Excellent time management
   - 75-89%: Good reliability
   - 60-74%: Acceptable
   - <60%: Poor deadline adherence

3. TASK COMPLEXITY (20% weight):
   - Priority 7-10: Handles critical tasks
   - Priority 5-6: Medium priority tasks
   - Priority 3-4: Standard tasks
   - Priority 1-2: Simple tasks

4. DIFFICULTY HANDLING (20% weight):
   - Difficulty 7-10: Expert level
   - Difficulty 5-6: Intermediate
   - Difficulty 3-4: Basic
   - Difficulty 1-2: Beginner

📋 SKILL LEVEL DEFINITIONS:
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
INTERN (0-2): Learning basics, needs guidance
FRESHER (2-4): Can handle simple tasks independently
JUNIOR (4-6): Competent with routine tasks
MEDIUM (6-8): Handles complex tasks reliably
SENIOR (8-9.5): Expert performer, mentors others
EXPERT (9.5-10): Top tier, exceptional performance

⚠️ IMPORTANT: Return ONLY valid JSON (no markdown, no backticks, no explanation):

{
    "skillScore": 7.5,
    "skillLevel": "MEDIUM",
    "insights": [
        "Specific insight about completion rate",
        "Specific insight about time management",
        "Specific insight about task complexity"
    ]
}

The skillScore must be between 0 and 10.
The skillLevel must be one of: INTERN, FRESHER, JUNIOR, MEDIUM, SENIOR, EXPERT
Provide 3-5 specific, actionable insights.
        """.trimIndent()
    }

    // 🆕 Task Summary Prompt
    fun getTaskSummaryPrompt(tasks: List<Task>): String {
        val dateFormatter = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())

        val tasksSummary = tasks.take(10).joinToString("\n") { task ->
            val status = when (task.status) {
                TaskStatus.TODO -> "📝 To Do"
                TaskStatus.IN_PROGRESS -> "⏳ In Progress"
                TaskStatus.DONE -> "✅ Done"
                TaskStatus.BLOCKED -> "🚫 Blocked"
            }

            val dueDate = task.dueAt?.let { dateFormatter.format(it) } ?: "No deadline"

            """
            • ${task.title}
              Status: $status
              Priority: ${task.priority.displayName}
              Difficulty: ${task.difficulty.displayName}
              Due: $dueDate
            """.trimIndent()
        }

        return """
Summarize these tasks for the user in a clear, organized format:

$tasksSummary

${if (tasks.size > 10) "\n... and ${tasks.size - 10} more tasks" else ""}

Provide:
1. Overall status summary
2. Urgent items that need attention
3. Recommended priorities
4. Brief motivational message
        """.trimIndent()
    }

    fun formatTasksForAnalysis(tasks: List<Task>): String {
        return buildString {
            appendLine("TASK LIST FOR ANALYSIS:")
            appendLine("━━━━━━━━━━━━━━━━━━━━━━")
            tasks.forEachIndexed { index, task ->
                appendLine()
                appendLine("Task ${index + 1}:")
                appendLine("  Title: ${task.title}")
                appendLine("  Status: ${task.status.name}")
                appendLine("  Priority: ${task.priority.name}/10")
                appendLine("  Difficulty: ${task.difficulty.name}/10")

                task.dueAt?.let { due ->
                    val isLate = task.status == TaskStatus.DONE && task.updatedAt > due
                    appendLine("  Completed: ${if (isLate) "LATE" else "ON TIME"}")
                }

                val completionTime = if (task.status == TaskStatus.DONE) {
                    val start = task.startAt ?: task.createdAt
                    val end = task.updatedAt
                    val days = TimeUnit.MILLISECONDS.toDays(end.time - start.time)
                    "  Time taken: $days days"
                } else {
                    "  Still in progress"
                }
                appendLine(completionTime)
            }
        }
    }
}