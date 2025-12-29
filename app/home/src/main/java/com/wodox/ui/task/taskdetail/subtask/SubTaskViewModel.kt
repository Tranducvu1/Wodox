package com.wodox.ui.task.taskdetail.subtask

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.wodox.core.base.viewmodel.BaseUiStateViewModel
import com.wodox.core.extension.serializable
import com.wodox.domain.home.model.local.Attachment
import com.wodox.domain.home.model.local.SubTask
import com.wodox.domain.home.model.local.Task
import com.wodox.domain.home.usecase.SaveAttachmentUseCase
import com.wodox.domain.home.usecase.subtask.SaveSubTaskUseCase
import com.wodox.model.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Date
import java.util.UUID
import javax.inject.Inject


@HiltViewModel
class SubTaskViewModel @Inject constructor(
     val app: Application,
    private val saveSubTaskUseCase: SaveSubTaskUseCase,
    private val saveAttachmentUseCase: SaveAttachmentUseCase,
) : BaseUiStateViewModel<SubTaskUiState, SubTaskUiEvent, SubTaskUiAction>(app) {

    override fun initialState(): SubTaskUiState = SubTaskUiState()

    override fun onCreate() {
        super.onCreate()
        loadsSubTask()
    }

    val currentTask = MutableLiveData<Task>()

    val task by lazy {
        data?.serializable<Task>(Constants.Intents.TASK)
    }

    val subTask by lazy {
        data?.serializable<SubTask>(Constants.Intents.SUB_TASK)
    }

    override fun handleAction(action: SubTaskUiAction) {
        super.handleAction(action)
        when (action) {
            is SubTaskUiAction.DeleteAttachment -> deleteAttachment(action.attachment)
            is SubTaskUiAction.SaveSubTask -> saveSubTask(action.subtask)
        }
    }

    private fun loadsSubTask() {
        viewModelScope.launch {
            updateState {
                it.copy(
                    subTasks = subTask
                )
            }
        }
    }

    private fun deleteAttachment(attachment: Attachment) {
        viewModelScope.launch(Dispatchers.IO) {
            attachment.deletedAt = Date()
            saveAttachmentUseCase(attachment)
            sendEvent(SubTaskUiEvent.DeleteSuccess)
        }
    }

    private fun saveSubTask(subtask: SubTask) {
        viewModelScope.launch(Dispatchers.IO) {
            val subTask = SubTask(
                id = UUID.randomUUID(),
                taskId = subtask.taskId,
                title = subtask.title,
                description = subtask.description,
                startAt = subtask.startAt,
                dueAt = subtask.dueAt,
                priority = subtask.priority,
                createdAt = Date(),
                updatedAt = Date()
            )
            saveSubTaskUseCase(subTask)
//                val attachment = Attachment(
//                    id = UUID.randomUUID(),
//                    taskId = taskID,
//                    uri = uri.toString(),
//                    type = type,
//                    name = fileName
//                )
//                saveAttachmentUseCase(attachment)
            sendEvent(SubTaskUiEvent.SaveSuccess)
        }
    }
}
