package com.wodox.ui.task.menuoption

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.wodox.core.base.viewmodel.BaseUiStateViewModel
import com.wodox.core.extension.serializable
import com.wodox.domain.home.model.local.MenuOption
import com.wodox.domain.home.model.local.Task
import com.wodox.domain.home.usecase.log.DeleteAllLogsByTaskIdUseCase
import com.wodox.domain.home.usecase.GetAllMenuOptionUseCase
import com.wodox.domain.home.usecase.task.SaveTaskUseCase
import com.wodox.domain.user.usecase.GetUserUseCase
import com.wodox.model.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Date
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class OptionMenuUiViewModel @Inject constructor(
    val app: Application,
    private val getAllMenuOptionUseCase: GetAllMenuOptionUseCase,
    private val saveTaskUseCase: SaveTaskUseCase,
    private val getUserUseCase: GetUserUseCase,
    private val deleteAllLogsByTaskIdUseCase: DeleteAllLogsByTaskIdUseCase,
) : BaseUiStateViewModel<OptionMenuUiState, OptionMenuUiEvent, OptionMenuUiAction>(app) {
    override fun initialState(): OptionMenuUiState = OptionMenuUiState()


    val task by lazy {
        data?.serializable<Task>(Constants.Intents.TASK)
    }

    val item: StateFlow<List<MenuOption>> = getAllMenuOptionUseCase().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = emptyList()
    )

    override fun handleAction(action: OptionMenuUiAction) {
        when (action) {
            is OptionMenuUiAction.DeleteTask -> deleteTask()
            is OptionMenuUiAction.DuplicateTask -> handleDuplicate()
            is OptionMenuUiAction.RemindTask -> handleReminder()
        }
    }

    private fun deleteTask() {
        val taskId = task ?: return
        viewModelScope.launch(Dispatchers.IO) {
            val deleteTaskUpdate = taskId.copy(
                deletedAt = Date()
            )
            val userId = getUserUseCase() ?: return@launch
            if (deleteTaskUpdate.ownerId != userId) return@launch
            saveTaskUseCase(deleteTaskUpdate)
            deleteAllLogsByTaskIdUseCase(taskId.id)
            sendEvent(OptionMenuUiEvent.DeleteSuccess)
        }
    }

    private fun handleDuplicate() {
        val originalTask = task ?: return
        viewModelScope.launch(Dispatchers.IO) {
            val duplicatedTask = originalTask.copy(
                id = UUID.randomUUID(),
                projectId = originalTask.projectId,
                title = "Copy of ${originalTask.title}",
                description = "Copy of ${originalTask.description}",
                status = originalTask.status,
                priority = originalTask.priority,
                startAt = originalTask.startAt,
                dueAt = originalTask.dueAt,
                createdAt = Date(),
                updatedAt = Date(),
            )
            saveTaskUseCase(duplicatedTask)
            sendEvent(OptionMenuUiEvent.DuplicateSuccess)
        }
    }

    private fun handleReminder() {
        val t = task ?: return

        val start = t.startAt?.time
        val due = t.dueAt?.time

        sendEvent(
            OptionMenuUiEvent.RemindSuccess(
                message = t.title.ifEmpty { "Your task reminder" }, startTime = start, dueTime = due
            )
        )
    }

}