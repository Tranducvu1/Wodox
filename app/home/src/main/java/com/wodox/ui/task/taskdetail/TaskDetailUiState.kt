package com.wodox.ui.task.taskdetail

import com.wodox.domain.home.model.local.Attachment
import com.wodox.domain.home.model.local.SubTask
import com.wodox.domain.home.model.local.TaskAnalysisResult
import com.wodox.domain.user.model.User
import java.util.UUID

data class TaskDetailUiState (
    val attachments: List<Attachment> = emptyList(),
    val subTasks:List<SubTask> = emptyList(),
    val users: List<User> = emptyList(),
    val taskId: UUID? = null,
    val user:User? = null,
    val currentUserId :UUID? = null,
    val isLoading: Boolean = true,
    val isLoadingSupporters: Boolean = false,
    val isAnalyzing: Boolean = false,
    val userSkillAnalysis: TaskAnalysisResult? = null
)