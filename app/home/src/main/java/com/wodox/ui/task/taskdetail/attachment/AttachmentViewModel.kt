package com.wodox.ui.task.taskdetail.attachment

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.wodox.core.base.viewmodel.BaseUiStateViewModel
import com.wodox.domain.home.model.local.Task
import com.wodox.domain.home.model.local.TaskStatus
import com.wodox.domain.home.usecase.SaveTaskUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class AttachmentViewModel @Inject constructor(
     val app: Application,
    private val saveTaskUseCase: SaveTaskUseCase
) : BaseUiStateViewModel<AttachmentUiState, AttachmentUiEvent, AttachmentUiAction>(app) {
    override fun initialState(): AttachmentUiState = AttachmentUiState()
    val currentTask = MutableLiveData<Task>()

    override fun handleAction(action: AttachmentUiAction) {
        super.handleAction(action)
        when (action) {

        }
    }

    private fun updateTaskStatus(status: TaskStatus) {
        viewModelScope.launch(Dispatchers.IO) {
            currentTask.value?.let { task ->
                val updatedTask = task.copy(status = status)
                currentTask.postValue(updatedTask)
                saveTaskUseCase(updatedTask)
            }
        }
    }

    private fun updateDate(start: Long, end: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            currentTask.value?.let { task ->
                val updatedTask = task.copy(
                    startAt = Date(start),
                    dueAt = Date(end)
                )
                currentTask.postValue(updatedTask)
                saveTaskUseCase(updatedTask)
            }
        }
    }

}