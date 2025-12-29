package com.wodox.ui.task.taskdetail.createtask

import com.wodox.domain.home.model.local.TaskAnalysisResult
import com.wodox.domain.user.model.User


sealed class CreateTaskUiEvent {
    data object SaveSuccess : CreateTaskUiEvent()
    data object DeleteSuccess : CreateTaskUiEvent()
    data class Error(val message: String) : CreateTaskUiEvent()

    object AssignSuccess : CreateTaskUiEvent()

    data class AnalysisComplete(val result: TaskAnalysisResult) : CreateTaskUiEvent()
    data class SupportersLoaded(val supporters: List<User>) : CreateTaskUiEvent()
}