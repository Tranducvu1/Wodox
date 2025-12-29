package com.wodox.ui.task.aibottomsheet

sealed class AiResponseUiEvent {
    data class HandleGenerate(val message: String) : AiResponseUiEvent()
    data class Error(val error: String) : AiResponseUiEvent()
}