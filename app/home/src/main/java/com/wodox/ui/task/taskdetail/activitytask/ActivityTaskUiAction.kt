package com.wodox.ui.task.taskdetail.activitytask

import com.wodox.domain.home.model.local.Comment
import java.util.UUID

sealed class ActivityTaskUiAction {
    object LoadActivity : ActivityTaskUiAction()
    object LoadComments : ActivityTaskUiAction()
    data class SendComment(val content: String) : ActivityTaskUiAction()
    data class UpdateComment(val commentId: UUID, val content: String) : ActivityTaskUiAction()
    data class DeleteComment(val commentId: UUID) : ActivityTaskUiAction()
    data class StartEditComment(val comment: Comment) : ActivityTaskUiAction()
    object CancelEditComment : ActivityTaskUiAction()
}