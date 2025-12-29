package com.wodox.ui.task.taskdetail.activitytask

sealed class ActivityTaskUiEvent {
    data class ShowError(val message: String) : ActivityTaskUiEvent()
    object CommentSentSuccess : ActivityTaskUiEvent()
    object CommentDeleteSuccess : ActivityTaskUiEvent()
}
