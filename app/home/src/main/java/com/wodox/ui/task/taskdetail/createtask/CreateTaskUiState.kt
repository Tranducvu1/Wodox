package com.wodox.ui.task.taskdetail.createtask

import com.wodox.domain.home.model.local.Attachment
import com.wodox.domain.home.model.local.Task
import com.wodox.domain.home.model.local.TaskAnalysisResult

data class CreateTaskUiState(
    val tasks: Task? = null,
    val isLoadingSupporters: Boolean = false,
    val isAnalyzing: Boolean = false,
    val attachments: List<Attachment> = emptyList(),
    val userSkillAnalysis: TaskAnalysisResult? = null
)