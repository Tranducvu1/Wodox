package com.wodox.ui.task.taskdetail.description

import android.app.Application
import androidx.databinding.ObservableBoolean
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.wodox.core.base.viewmodel.BaseUiStateViewModel
import com.wodox.domain.docs.model.TextFormat
import com.wodox.domain.home.model.local.Task
import com.wodox.domain.home.usecase.task.SaveTaskUseCase
import com.wodox.model.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DescriptionUiViewModel @Inject constructor(
     val app: Application,
    private val saveTaskUseCase: SaveTaskUseCase,
    private val savedStateHandle: SavedStateHandle
) : BaseUiStateViewModel<DescriptionUiState, DescriptionUiEvent, DescriptionUiAction>(app) {
    private val task: Task? = savedStateHandle.get(Constants.Intents.TASK)

    override fun initialState(): DescriptionUiState = DescriptionUiState()

    init {
        val t = task
        t?.let {
            updateState {
                it.copy(
                    task = t
                )
            }
        }
    }

    val visibleKeyboard = ObservableBoolean(false)
    val isFormatFont = ObservableBoolean(false)
    var textFormat: TextFormat = TextFormat()

    override fun handleAction(action: DescriptionUiAction) {
        when (action) {
            is DescriptionUiAction.SaveDescription -> saveFormattedText(action.description)
        }
    }

    fun onTextChangeDescription(text: CharSequence?) {
        val current = uiState.value.task ?: return
        val updated = current.copy(description = text.toString())
        updateState { it.copy(task = updated) }
    }

    fun saveFormattedText(editable: String?) {
        val currentTask = uiState.value.task ?: return
        val updatedTask = currentTask.copy(description = editable)
        viewModelScope.launch(Dispatchers.IO) {
            saveTaskUseCase(updatedTask)
            sendEvent(DescriptionUiEvent.SaveSuccess)
        }
    }
}
