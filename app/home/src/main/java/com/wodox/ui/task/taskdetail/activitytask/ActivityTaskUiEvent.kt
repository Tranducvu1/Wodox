package com.wodox.ui.task.taskdetail.activitytask

import com.wodox.domain.home.model.local.Comment

sealed class ActivityTaskUiEvent {
    data class ShowError(val message: String) : ActivityTaskUiEvent()
    object CommentSentSuccess : ActivityTaskUiEvent()
    object CommentUpdateSuccess : ActivityTaskUiEvent()
    object CommentDeleteSuccess : ActivityTaskUiEvent()
    data class StartEditMode(val comment: Comment) : ActivityTaskUiEvent()
    object CancelEditMode : ActivityTaskUiEvent()
}
