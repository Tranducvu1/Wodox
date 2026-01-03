package com.wodox.mywork.ui

import com.wodox.domain.home.model.local.Priority
import com.wodox.domain.home.model.local.TaskStatus
import com.wodox.domain.mywork.TaskFilterType
import com.wodox.domain.mywork.TaskSortType
import com.wodox.domain.mywork.TaskViewType
import java.util.UUID

sealed class MyWorkUiAction {
    object LoadTasks : MyWorkUiAction()
    object LoadNotifications : MyWorkUiAction()
    object LoadComments : MyWorkUiAction()

    data class FilterByStatus(val status: TaskStatus?) : MyWorkUiAction()
    data class FilterByPriority(val priority: Priority?) : MyWorkUiAction()
    data class SortTasks(val sortType: TaskSortType) : MyWorkUiAction()
    data class ChangeViewType(val viewType: TaskViewType) : MyWorkUiAction()

    data class SendTaskReminder(
        val taskId: UUID,
        val taskName: String,
        val title: String,
        val description: String,
        val endTime: Long,
        val notificationType: String
    ) : MyWorkUiAction()

    data class MarkNotificationAsRead(val notificationId: UUID) : MyWorkUiAction()
    data class SetCurrentUserId(val userId: UUID) : MyWorkUiAction()
}