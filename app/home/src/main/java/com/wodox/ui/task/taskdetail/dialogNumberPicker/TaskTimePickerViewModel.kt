package com.wodox.ui.task.taskdetail.dialogNumberPicker

import android.app.Application
import com.wodox.core.base.viewmodel.BaseUiStateViewModel
import com.wodox.domain.home.usecase.task.SaveTaskUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class TaskTimePickerViewModel @Inject constructor(
      application: Application,
    private val saveTaskUseCase: SaveTaskUseCase
) : BaseUiStateViewModel<TaskTimePickerUiState, TaskTimePickerUiEvent, TaskTimePickerUiAction>(application) {
    override fun initialState(): TaskTimePickerUiState = TaskTimePickerUiState()
}
