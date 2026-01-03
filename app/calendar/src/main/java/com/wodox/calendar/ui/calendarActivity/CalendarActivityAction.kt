package com.wodox.calendar.ui.calendarActivity

import com.wodox.domain.home.model.local.Task
import com.wodox.domain.home.model.local.TaskStatus

sealed class CalendarActivityAction {
    data class SelectDate(val dateMillis: Long) : CalendarActivityAction()
    object PreviousDay : CalendarActivityAction()
    object NextDay : CalendarActivityAction()
    data class UpdateTaskStatus(val task: Task, val newStatus: TaskStatus) : CalendarActivityAction()
}