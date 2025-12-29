package com.wodox.ui.task.taskdetail.subtask

import com.wodox.domain.home.model.local.Attachment
import com.wodox.domain.home.model.local.SubTask
import com.wodox.domain.home.model.local.Task

data class SubTaskUiState (
    val task : Task? = null,
    val attachments: List<Attachment> = emptyList(),
    val subTasks: SubTask? = null,
)