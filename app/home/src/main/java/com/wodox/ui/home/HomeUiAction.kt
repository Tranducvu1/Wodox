package com.wodox.ui.home

import com.wodox.domain.home.model.local.Task

sealed class HomeUiAction {
    data class UpdateFavourite(val task: Task) : HomeUiAction()

    data class UpdateSearchQuery(val query: String) : HomeUiAction()

}
