package com.wodox.calendar.ui.calendarActivity

import com.wodox.domain.home.model.local.Task

data class CalendarActivityUiState(
    val userId: String = "",
    val selectedDate: Long = System.currentTimeMillis(),
    val tasksOfSelectedDate: List<Task> = emptyList()
)