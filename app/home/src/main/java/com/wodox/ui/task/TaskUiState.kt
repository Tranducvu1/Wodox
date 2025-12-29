package com.wodox.ui.task

import com.wodox.domain.home.model.local.Task
import com.wodox.ui.task.menu.TaskBarMenu

data class TaskUiState(
    val task: Task? = null,
    val menusTopbar: List<TaskBarMenu> = arrayListOf()
)