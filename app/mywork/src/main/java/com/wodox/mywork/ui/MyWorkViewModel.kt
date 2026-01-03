package com.wodox.mywork.ui



import android.app.Application
import androidx.lifecycle.viewModelScope
import com.wodox.core.base.viewmodel.BaseUiStateViewModel
import com.wodox.domain.chat.usecase.GetNotificationByUserIdUseCase
import com.wodox.domain.chat.usecase.MarkAsReadNotificationUseCase
import com.wodox.domain.chat.usecase.Params
import com.wodox.domain.chat.usecase.SendTaskReminderUseCase
import com.wodox.domain.home.model.local.Priority
import com.wodox.domain.home.model.local.Task
import com.wodox.domain.home.model.local.TaskStatus
import com.wodox.domain.home.usecase.comment.GetLatestUnreadCommentUseCase
import com.wodox.domain.home.usecase.task.GetTaskUseCase
import com.wodox.domain.home.usecase.task.GetTasksByStatusUseCase
import com.wodox.domain.home.usecase.task.GetCompletedTasksUseCase
import com.wodox.domain.home.usecase.task.GetPendingTasksUseCase
import com.wodox.domain.home.usecase.task.GetOverdueTasksUseCase
import com.wodox.domain.home.usecase.task.GetTasksByPriorityUseCase
import com.wodox.domain.home.usecase.task.GetTasksSortedByPriorityUseCase
import com.wodox.domain.home.usecase.task.GetTasksSortedByNameUseCase
import com.wodox.domain.mywork.TaskSortType
import com.wodox.domain.mywork.TaskViewType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class MyWorkViewModel @Inject constructor(
    val app: Application,
    private val getTaskUseCase: GetTaskUseCase,
    private val getLatestUnreadCommentUseCase: GetLatestUnreadCommentUseCase,
    private val sendTaskReminderUseCase: SendTaskReminderUseCase,
    private val getNotificationsByUserIdUseCase: GetNotificationByUserIdUseCase,
    private val markNotificationAsReadUseCase: MarkAsReadNotificationUseCase,
) : BaseUiStateViewModel<MyWorkUiState, MyWorkUiEvent, MyWorkUiAction>(app) {

    override fun initialState(): MyWorkUiState = MyWorkUiState()
    private var currentUserId: UUID? = null

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            loadTasks()
            observeLatestComment()
        }
    }

    override fun handleAction(action: MyWorkUiAction) {
        when (action) {
            is MyWorkUiAction.LoadTasks -> loadTasks()
            is MyWorkUiAction.LoadNotifications -> loadNotifications()
            is MyWorkUiAction.LoadComments -> observeLatestComment()
            is MyWorkUiAction.FilterByStatus -> filterByStatus(action.status)
            is MyWorkUiAction.FilterByPriority -> filterByPriority(action.priority)
            is MyWorkUiAction.SortTasks -> sortTasks(action.sortType)
            is MyWorkUiAction.ChangeViewType -> changeViewType(action.viewType)
            is MyWorkUiAction.SendTaskReminder -> sendTaskReminder(action)
            is MyWorkUiAction.MarkNotificationAsRead -> markNotificationAsRead(action.notificationId)
            is MyWorkUiAction.SetCurrentUserId -> setCurrentUserId(action.userId)
        }
    }

    private fun loadTasks() {
        viewModelScope.launch {
            updateState { it.copy(isLoading = true) }
            try {
                getTaskUseCase.execute().collect { tasks ->
                    val filteredTasks = applyFilters(tasks)
                    val sortedTasks = applySort(filteredTasks)
                    updateState { it.copy(tasks = sortedTasks, isLoading = false) }
                }
            } catch (e: Exception) {
                updateState { it.copy(error = e.message, isLoading = false) }
                sendEvent(MyWorkUiEvent.ShowError(e.message ?: "Unknown error"))
            }
        }
    }

    private fun loadNotifications() {
        viewModelScope.launch {
            currentUserId?.let { userId ->
                try {
                    getNotificationsByUserIdUseCase.execute(userId).collect { notifications ->
                        updateState { it.copy(notifications = notifications) }
                    }
                } catch (e: Exception) {
                    updateState { it.copy(error = e.message) }
                }
            }
        }
    }

    private fun observeLatestComment() {
        viewModelScope.launch {
            try {
                getLatestUnreadCommentUseCase(currentUserId ?: UUID.randomUUID())
                    .collect { comment ->
                        updateState { it.copy(latestComment = comment) }
                    }
            } catch (e: Exception) {
                updateState { it.copy(error = e.message) }
            }
        }
    }

    private fun filterByStatus(status: TaskStatus?) {
        viewModelScope.launch {
            updateState { it.copy(filterStatus = status) }
            loadTasks()
        }
    }

    private fun filterByPriority(priority: Priority?) {
        viewModelScope.launch {
            updateState { it.copy(filterPriority = priority) }
            loadTasks()
        }
    }

    private fun sortTasks(sortType: TaskSortType) {
        viewModelScope.launch {
            updateState { it.copy(sortType = sortType) }
            loadTasks()
        }
    }

    private fun changeViewType(viewType: TaskViewType) {
        viewModelScope.launch {
            updateState { it.copy(viewType = viewType) }
            sendEvent(MyWorkUiEvent.ShowMessage("View changed to ${viewType.name}"))
        }
    }

    private fun sendTaskReminder(action: MyWorkUiAction.SendTaskReminder) {
        viewModelScope.launch {
            try {
                val fromUserId = currentUserId ?: UUID.randomUUID()
                val params = Params(
                    taskId = action.taskId,
                    taskName = action.taskName,
                    title = action.title,
                    description = action.description,
                    toUserId = action.taskId,
                    fromUserId = fromUserId,
                    fromUserName = "You",
                    userAvatar = "",
                    endTime = action.endTime,
                    notificationType = action.notificationType
                )
                sendTaskReminderUseCase.execute(params)
                sendEvent(MyWorkUiEvent.NotificationSent("Reminder sent."))
                loadNotifications()
            } catch (e: Exception) {
                sendEvent(MyWorkUiEvent.ShowError(e.message ?: "Failed to send reminder"))
                updateState { it.copy(error = e.message) }
            }
        }
    }

    private fun markNotificationAsRead(notificationId: UUID) {
        viewModelScope.launch {
            try {
                markNotificationAsReadUseCase.execute(notificationId)
                loadNotifications()
            } catch (e: Exception) {
                updateState { it.copy(error = e.message) }
            }
        }
    }

    private fun setCurrentUserId(userId: UUID) {
        currentUserId = userId
        loadNotifications()
    }

    private fun applyFilters(tasks: List<Task>): List<Task> {
        var filtered = tasks

        uiState.value.filterStatus?.let { status ->
            filtered = filtered.filter { it.status == status }
        }

        uiState.value.filterPriority?.let { priority ->
            filtered = filtered.filter { it.priority == priority }
        }

        return filtered
    }

    private fun applySort(tasks: List<Task>): List<Task> {
        return when (uiState.value.sortType) {
            TaskSortType.BY_DATE -> tasks.sortedBy { it.dueAt?.time ?: Long.MAX_VALUE }
            TaskSortType.BY_PRIORITY -> tasks.sortedByDescending { it.priority.value }
            TaskSortType.BY_NAME -> tasks.sortedBy { it.title }
        }
    }
}