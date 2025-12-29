package com.wodox.ui.task.taskdetail.checklist

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.wodox.core.base.viewmodel.BaseUiStateViewModel
import com.wodox.core.extension.serializable
import com.wodox.domain.home.model.local.CheckList
import com.wodox.domain.home.model.local.Task
import com.wodox.domain.home.usecase.checklist.GetCheckListByTaskIdUseCase
import com.wodox.domain.home.usecase.checklist.SaveCheckListUseCase
import com.wodox.model.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Date
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class CheckListViewModel @Inject constructor(
     val app: Application,
    private val saveCheckListUseCase: SaveCheckListUseCase,
    private val getCheckListByTaskIdUseCase: GetCheckListByTaskIdUseCase
) : BaseUiStateViewModel<CheckListUiState, CheckListUiEvent, CheckListUiAction>(app) {

    override fun initialState(): CheckListUiState = CheckListUiState()

    private val task by lazy {
        data?.serializable<Task>(Constants.Intents.TASK)
    }

    override fun handleAction(action: CheckListUiAction) {
        when (action) {
            is CheckListUiAction.AddNewDescription -> handleUpdateDescription(action.description)
        }
    }

    override fun onCreate() {
        super.onCreate()
        loadCheckList()
    }

    private fun loadCheckList() {
        val taskId = task?.id ?: return
        viewModelScope.launch(Dispatchers.IO) {
            getCheckListByTaskIdUseCase(taskId).collect { checklist ->
                updateState {
                    it.copy(checkList = checklist)
                }
            }
        }
    }

    private fun handleUpdateDescription(description: String) {
        val taskId = task ?: return
        viewModelScope.launch(Dispatchers.IO) {
            val checkList = CheckList(
                id = UUID.randomUUID(),
                taskId = taskId.id,
                description = description,
                createdAt = Date(),
                updatedAt = Date()
            )

            saveCheckListUseCase(checkList)
            sendEvent(CheckListUiEvent.SuccessUpdate)
        }
    }
}
