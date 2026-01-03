package com.wodox.main.ui.main.aiassistant


sealed class AIBottomSheetUiEvent {
    data class HandleGenerate(val message: String) : AIBottomSheetUiEvent()
    data class Error(val error: String) : AIBottomSheetUiEvent()
}