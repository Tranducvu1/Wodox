package com.wodox.ui.task.taskdetail.dialogDayPicker

import android.app.Application
import com.wodox.core.base.viewmodel.BaseUiStateViewModel
import com.wodox.domain.home.usecase.task.SaveTaskUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TaskDatePickerViewModel @Inject constructor(
     val app: Application,
    private val saveTaskUseCase: SaveTaskUseCase
) : BaseUiStateViewModel<TaskDatePickerUiState, TaskDatePickerUiEvent, TaskDatePickerUiAction>(app) {
    override fun initialState(): TaskDatePickerUiState = TaskDatePickerUiState()
}