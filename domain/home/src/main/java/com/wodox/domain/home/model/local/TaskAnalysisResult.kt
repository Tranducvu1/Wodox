package com.wodox.domain.home.model.local

import com.wodox.domain.user.model.SkillLevel
import java.util.Date
import java.util.UUID

data class TaskAnalysisResult(
    val userId: UUID,
    val totalTasks: Int,
    val completedTasks: Int,
    val onTimeTasks: Int,
    val lateTasks: Int,
    val averagePriority: Double,
    val averageDifficulty: Double,
    val averageCompletionDays: Double,
    val skillScore: Double,
    val suggestedLevel: SkillLevel,
    val analyzedAt: Date,
    val insights: List<String>
)