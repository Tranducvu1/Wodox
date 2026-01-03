package com.wodox.main.ui.main.aiassistant


sealed class AIBottomSheetUiAction {
    data class SendMessage(val message: String) : AIBottomSheetUiAction()

    object LoadHistory :  AIBottomSheetUiAction()
}