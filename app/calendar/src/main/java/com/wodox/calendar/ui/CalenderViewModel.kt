package com.wodox.calendar.ui

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.wodox.core.base.viewmodel.BaseUiStateViewModel
import com.wodox.domain.home.model.local.Task
import com.wodox.domain.home.model.local.TaskStatus
import com.wodox.domain.home.model.local.Log
import com.wodox.domain.home.usecase.GetAllTaskUseCase
import com.wodox.domain.home.usecase.GetTaskByTaskIdUseCase
import com.wodox.domain.home.usecase.GetTaskCalendarUseCase
import java.util.Date
import com.wodox.domain.home.usecase.GetTaskUseCase
import com.wodox.domain.home.usecase.SaveTaskUseCase
import com.wodox.domain.home.usecase.SaveLogUseCase
import com.wodox.domain.user.usecase.GetUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class CalenderViewModel @Inject constructor(
    val app: Application,
    private val getTaskUseCase: GetTaskCalendarUseCase,
    private val getUserUseCase: GetUserUseCase,
    private val saveTaskUseCase: SaveTaskUseCase,
    private val saveLogUseCase: SaveLogUseCase,
) : BaseUiStateViewModel<CalenderUiState, CalenderUiEvent, CalenderUiAction>(app) {

    val allTasks: StateFlow<List<Task>> = getTaskUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = emptyList()
        )

    override fun initialState(): CalenderUiState = CalenderUiState()

    override fun handleAction(action: CalenderUiAction) {
        when (action) {
            is CalenderUiAction.UpdateSelectedDate ->
                updateSelectedDate(action.newDateMillis)

            is CalenderUiAction.UpdateTaskStatus ->
                updateTaskStatus(action.task, action.newStatus)
        }
    }

    init {
        loadUser()
    }

    private fun loadUser() {
        viewModelScope.launch(Dispatchers.IO) {
            val userId = getUserUseCase()
            updateState {
                it.copy(
                    userId = userId
                )
            }
        }
    }

    private fun updateSelectedDate(dateMillis: Long) {
        val filtered = allTasks.value.filter {
            it.isInSameDay(dateMillis)
        }
        updateState {
            it.copy(
                selectedDate = dateMillis,
                tasksOfSelectedDate = filtered
            )
        }
    }

    private fun updateTaskStatus(task: Task, newStatus: TaskStatus) {
        viewModelScope.launch(Dispatchers.IO) {
            val updatedTask = task.copy(status = newStatus)
            saveTaskUseCase.invoke(updatedTask)
            val log = Log(
                taskId = task.id,
                title = "Status Changed",
                description = "${task.status.name} → ${newStatus.name}",
                createdAt = Date()
            )
            saveLogUseCase(log)
        }
    }

    fun Task.isInSameDay(dateMillis: Long): Boolean {
        val taskDate = dueAt ?: return false

        val cal1 = java.util.Calendar.getInstance().apply {
            time = taskDate
            set(java.util.Calendar.HOUR_OF_DAY, 0)
            set(java.util.Calendar.MINUTE, 0)
            set(java.util.Calendar.SECOND, 0)
            set(java.util.Calendar.MILLISECOND, 0)
        }

        val cal2 = java.util.Calendar.getInstance().apply {
            timeInMillis = dateMillis
            set(java.util.Calendar.HOUR_OF_DAY, 0)
            set(java.util.Calendar.MINUTE, 0)
            set(java.util.Calendar.SECOND, 0)
            set(java.util.Calendar.MILLISECOND, 0)
        }

        return cal1.timeInMillis == cal2.timeInMillis
    }
}