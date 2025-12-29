package com.wodox.ui.task.taskdetail.subtask

import com.wodox.ui.task.taskdetail.TaskDetailUiEvent

sealed class SubTaskUiEvent {
    data object SaveSuccess : SubTaskUiEvent()
    data object DeleteSuccess : SubTaskUiEvent()
    data class Error(val message: String) : SubTaskUiEvent()
}