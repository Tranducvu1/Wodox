package com.wodox.ui.task.aibottomsheet

sealed class AiResponseUiAction {
    data class SendMessage(val message: String) : AiResponseUiAction()

    object LoadHistory :  AiResponseUiAction()
}