package com.wodox.calendar.ui.calendarActivity

import android.app.Application
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.wodox.core.base.viewmodel.BaseUiStateViewModel
import com.wodox.domain.home.model.local.Log
import com.wodox.domain.home.model.local.Task
import com.wodox.domain.home.model.local.TaskStatus
import com.wodox.domain.home.usecase.log.SaveLogUseCase
import com.wodox.domain.home.usecase.task.GetTaskCalendarUseCase
import com.wodox.domain.home.usecase.task.SaveTaskUseCase
import com.wodox.domain.user.usecase.GetUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class CalendarActivityViewModel @Inject constructor(
    app: Application,
    private val getTaskUseCase: GetTaskCalendarUseCase,
    private val getUserUseCase: GetUserUseCase,
    private val saveTaskUseCase: SaveTaskUseCase,
    private val saveLogUseCase: SaveLogUseCase,
) : BaseUiStateViewModel<CalendarActivityUiState, CalendarActivityEvent, CalendarActivityAction>(app) {

    val allTasks: StateFlow<List<Task>> = getTaskUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    override fun initialState(): CalendarActivityUiState {
        val today = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

        return CalendarActivityUiState(selectedDate = today)
    }

    override fun handleAction(action: CalendarActivityAction) {
        when (action) {
            is CalendarActivityAction.SelectDate -> selectDate(action.dateMillis)
            is CalendarActivityAction.PreviousDay -> navigateToPreviousDay()
            is CalendarActivityAction.NextDay -> navigateToNextDay()
            is CalendarActivityAction.UpdateTaskStatus -> updateTaskStatus(action.task, action.newStatus)
        }
    }

    init {
        loadUser()
        observeTasks()
    }

    private fun loadUser() {
        viewModelScope.launch(Dispatchers.IO) {
            val userId = getUserUseCase()
            updateState { it.copy(userId = userId.toString()) }
        }
    }

    private fun observeTasks() {
        viewModelScope.launch {
            allTasks.collect { tasks ->
                updateTasksForSelectedDate(tasks)
            }
        }
    }

    private fun selectDate(dateMillis: Long) {
        updateState { it.copy(selectedDate = dateMillis) }
        updateTasksForSelectedDate(allTasks.value)
    }

    private fun navigateToPreviousDay() {
        val calendar = Calendar.getInstance().apply {
            timeInMillis = uiState.value.selectedDate
            add(Calendar.DAY_OF_MONTH, -1)
        }
        selectDate(calendar.timeInMillis)
    }

    private fun navigateToNextDay() {
        val calendar = Calendar.getInstance().apply {
            timeInMillis = uiState.value.selectedDate
            add(Calendar.DAY_OF_MONTH, 1)
        }
        selectDate(calendar.timeInMillis)
    }

    private fun updateTasksForSelectedDate(tasks: List<Task>) {
        val selectedDate = uiState.value.selectedDate
        val filtered = tasks.filter { task ->
            task.startAt?.let { taskDate ->
                isSameDay(taskDate.time, selectedDate)
            } ?: false
        }.sortedBy { it.startAt }

        updateState { it.copy(tasksOfSelectedDate = filtered) }
    }

    private fun updateTaskStatus(task: Task, newStatus: TaskStatus) {
        viewModelScope.launch(Dispatchers.IO) {
            val updatedTask = task.copy(status = newStatus)
            saveTaskUseCase(updatedTask)

            val log = Log(
                taskId = task.id,
                title = "Status Changed",
                description = "${task.status.name} → ${newStatus.name}",
                createdAt = Date()
            )
            saveLogUseCase(log)
        }
    }

    private fun isSameDay(time1: Long, time2: Long): Boolean {
        val cal1 = Calendar.getInstance().apply {
            timeInMillis = time1
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        val cal2 = Calendar.getInstance().apply {
            timeInMillis = time2
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        return cal1.timeInMillis == cal2.timeInMillis
    }
}


