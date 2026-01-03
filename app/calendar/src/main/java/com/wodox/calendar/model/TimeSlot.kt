package com.wodox.calendar.model

import com.wodox.domain.home.model.local.Task

data class TimeSlot(
    val hour: Int,
    val tasks: List<Task>
)