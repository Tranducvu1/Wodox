package com.wodox.calendar.ui

import com.wodox.domain.home.model.local.Task
import java.util.UUID

data class CalenderUiState(
    val selectedDate: Long? = null,
    val tasksOfSelectedDate: List<Task> = emptyList(),
    val userId: UUID? = null
)