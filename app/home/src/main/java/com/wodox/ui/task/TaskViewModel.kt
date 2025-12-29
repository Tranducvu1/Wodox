package com.wodox.ui.task

import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.wodox.core.base.viewmodel.BaseUiStateViewModel
import com.wodox.core.extension.toArrayList
import com.wodox.domain.home.model.local.Task
import com.wodox.domain.home.usecase.SaveTaskUseCase
import com.wodox.model.Constants
import com.wodox.ui.task.menu.TaskBarMenu
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TaskViewModel @Inject constructor(
    val app: Application,
    private val savedStateHandle: SavedStateHandle,
    private val saveTaskUseCase: SaveTaskUseCase,
    ) : BaseUiStateViewModel<TaskUiState, TaskUiEvent, TaskUiAction>(app) {
    override fun initialState(): TaskUiState = TaskUiState()

    val task: Task? by lazy {
        savedStateHandle.get<Task>(Constants.Intents.TASK)
    }

    init {
        val t = task
        t?.let {
            updateState {
                it.copy(
                    task = t
                )
            }
        }
        Log.d("TaskViewModel", "Task value = $t")
    }

    val topBarMenus: List<TaskBarMenu> = TaskBarMenu.getDefaults(applicationContext())

    val changePageEvent = MutableLiveData<Int>()

    override fun handleAction(action: TaskUiAction) {
        when (action) {
            is TaskUiAction.ChangeTab -> changeTaskTab(action.type)
            TaskUiAction.HandleUpdateFavourite -> updateFavourite()
        }
    }

    private fun changeTaskTab(type: TaskBarMenu.TaskBarMenuType) {
        val menus = uiState.value.menusTopbar.toArrayList().onEach {
            it.isSelected = it.type == type
        }
        updateState { it.copy(menusTopbar = menus) }
        changePageEvent.value = when (type) {
            TaskBarMenu.TaskBarMenuType.DETAIL -> 0
            TaskBarMenu.TaskBarMenuType.ACTIVITY -> 1
        }
    }

    private fun updateFavourite() {
        val currentTask = uiState.value.task ?: return
        viewModelScope.launch(Dispatchers.IO) {
            val updatedTask = currentTask.copy(
                isFavourite = !currentTask.isFavourite
            )
            saveTaskUseCase(updatedTask)
            updateState { it.copy(task = updatedTask) }
            sendEvent(TaskUiEvent.UpdateFavourite)
        }
    }

}