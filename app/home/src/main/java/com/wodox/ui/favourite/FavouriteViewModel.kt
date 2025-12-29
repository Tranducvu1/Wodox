package com.wodox.ui.favourite

import android.app.Application
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.wodox.core.base.viewmodel.BaseUiStateViewModel
import com.wodox.core.base.viewmodel.EmptyUiEvent
import com.wodox.domain.home.model.local.Task
import com.wodox.domain.home.usecase.SaveTaskUseCase
import com.wodox.domain.home.usecase.task.GetAllTaskFavourite
import com.wodox.domain.user.usecase.GetUserUseCase
import com.wodox.ui.home.HomeUiAction
import com.wodox.ui.home.HomeUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class FavouriteViewModel @Inject constructor(
     val app: Application,
    private val getAllTaskFavourite: GetAllTaskFavourite,
    private val saveTaskUseCase: SaveTaskUseCase,
    private val getUserUseCase: GetUserUseCase
) : BaseUiStateViewModel<HomeUiState, EmptyUiEvent, HomeUiAction>(app) {
    val taskFlow = flow {
        val userId = getUserUseCase() ?: return@flow
        getAllTaskFavourite(userId).collect { pagingData ->
            emit(pagingData)
        }
    }.cachedIn(viewModelScope)

    private val searchQueryFlow = MutableStateFlow("")

    override fun handleAction(action: HomeUiAction) {
        when (action) {
            is HomeUiAction.UpdateFavourite -> handleFavourite(action.task)
            is HomeUiAction.UpdateSearchQuery -> updateSearchQuery(action.query)
        }
    }

    override fun initialState(): HomeUiState = HomeUiState()

    private fun handleFavourite(task: Task) {
        val newTask = task.copy(isFavourite = !task.isFavourite)
        viewModelScope.launch(Dispatchers.IO) {
            saveTaskUseCase(newTask)
        }
    }

    private fun updateSearchQuery(query: String) {
        searchQueryFlow.value = query
    }
}