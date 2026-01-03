package com.wodox.mywork.ui

sealed class MyWorkUiEvent {
    data class ShowMessage(val message: String) : MyWorkUiEvent()
    data class ShowError(val error: String) : MyWorkUiEvent()
    data class ShowReminderDialog(val taskId: String, val taskName: String) : MyWorkUiEvent()
    data class NotificationSent(val message: String) : MyWorkUiEvent()
    object CloseDialog : MyWorkUiEvent()
}
