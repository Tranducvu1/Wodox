package com.wodox.ui.task.taskdetail

import com.wodox.domain.home.model.local.TaskAnalysisResult
import com.wodox.ui.task.taskdetail.createtask.CreateTaskUiEvent

sealed class TaskDetailUiEvent {
    object DeleteSuccess: TaskDetailUiEvent()
    object UpdateSuccess: TaskDetailUiEvent()

    object AssignSuccess : TaskDetailUiEvent()

    data class AnalysisComplete(val result: TaskAnalysisResult) : TaskDetailUiEvent()
    data class Error(val message: String) : TaskDetailUiEvent()


}