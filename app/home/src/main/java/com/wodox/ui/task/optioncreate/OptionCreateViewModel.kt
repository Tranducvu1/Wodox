package com.wodox.ui.task.optioncreate

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.wodox.core.base.viewmodel.BaseUiStateViewModel
import com.wodox.domain.home.model.local.Item
import com.wodox.domain.home.usecase.GetAllItemUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@HiltViewModel
class OptionCreateViewModel @Inject constructor(
    val app: Application,
    private val getAllItemUseCase: GetAllItemUseCase,
) : BaseUiStateViewModel<
        OptionCreateUiState,
        OptionCreateUiEvent,
        OptionCreateUiAction>(app) {

    override fun initialState(): OptionCreateUiState = OptionCreateUiState()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            getAllItemUseCase().collect { list ->
                if (list.isEmpty()) {
                    Log.d("OptionCreateVM", "No item -> set null")
                    updateState { it.copy(items = null) }
                } else {
                    Log.d("OptionCreateVM", "Have item -> size=${list.size}")
                    updateState { it.copy(items = list) }
                }
            }
        }
    }

    val item: StateFlow<List<Item>> =
        getAllItemUseCase()
            .onEach { list ->
                Log.d(
                    "OptionCreateVM",
                    "Item size = ${list.size}, items = $list"
                )
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyList()
            )
}
