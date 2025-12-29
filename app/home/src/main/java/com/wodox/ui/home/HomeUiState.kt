package com.wodox.ui.home

import com.wodox.domain.home.model.local.Task

data class HomeUiState(
    val tasks: List<Task> = emptyList(),
)