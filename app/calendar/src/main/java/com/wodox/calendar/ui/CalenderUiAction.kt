package com.wodox.calendar.ui

import com.wodox.domain.home.model.local.Task
import com.wodox.domain.home.model.local.TaskStatus

sealed class CalenderUiAction {
    data class UpdateSelectedDate(val newDateMillis: Long) : CalenderUiAction()
    data class UpdateTaskStatus(val task: Task, val newStatus: TaskStatus) : CalenderUiAction()
}