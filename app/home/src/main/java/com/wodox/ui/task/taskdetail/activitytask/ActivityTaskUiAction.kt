package com.wodox.ui.task.taskdetail.activitytask

import com.wodox.domain.home.model.local.Comment

sealed class ActivityTaskUiAction {
    object LoadActivity : ActivityTaskUiAction()
    object LoadComments : ActivityTaskUiAction()
    data class SendComment(val content: String) : ActivityTaskUiAction()
    data class DeleteComment(val commentId: java.util.UUID) : ActivityTaskUiAction()
}