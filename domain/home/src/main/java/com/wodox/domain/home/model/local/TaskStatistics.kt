package com.wodox.domain.home.model.local
 data class TaskStatistics(
    val totalTasks: Int,
    val completedTasks: Int,
    val onTimeTasks: Int,
    val lateTasks: Int,
    val avgPriority: Double,
    val avgDifficulty: Double,
    val avgCompletionDays: Double,
    val completionRate: Double,
    val onTimeRate: Double
)