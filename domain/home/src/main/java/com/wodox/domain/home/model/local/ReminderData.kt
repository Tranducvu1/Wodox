package com.wodox.domain.home.model.local

import java.util.Date

data class ReminderData(
    val date: Date,
    val music: Music?,
    val repeat: String
)