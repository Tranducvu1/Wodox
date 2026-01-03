package com.wodox.mywork.ui

import com.wodox.domain.chat.model.local.Notification
import com.wodox.domain.home.model.local.Comment
import com.wodox.domain.home.model.local.Priority
import com.wodox.domain.home.model.local.Task
import com.wodox.domain.home.model.local.TaskStatus
import com.wodox.domain.mywork.TaskFilterType
import com.wodox.domain.mywork.TaskSortType
import com.wodox.domain.mywork.TaskViewType


data class MyWorkUiState(
    val tasks: List<Task> = emptyList(),
    val notifications: List<Notification> = emptyList(),
    val latestComment: Comment? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val filterStatus: TaskStatus? = null,
    val filterPriority: Priority? = null,
    val filterType: TaskFilterType? = null,
    val sortType: TaskSortType = TaskSortType.BY_DATE,
    val viewType: TaskViewType = TaskViewType.LIST
)