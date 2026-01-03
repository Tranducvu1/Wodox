package com.wodox.ui.task.taskdetail.activitytask

import com.wodox.domain.home.model.local.Comment
import com.wodox.domain.home.model.local.Log
import java.util.UUID

data class ActivityTaskUiState(
    val listLogItem: List<Log> = emptyList(),
    val listComments: List<Comment> = emptyList(),
    val isLoadingComments: Boolean = false,
    val errorMessage: String? = null,
    val email: String? = "",
    val userId: UUID? = null,
    val editingCommentId: UUID? = null,
    val editingCommentContent: String? = null
)
