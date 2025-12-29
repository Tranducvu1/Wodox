package com.wodox.ui.task.menuoption

sealed class OptionMenuUiEvent {
    object DeleteSuccess : OptionMenuUiEvent()

    object DuplicateSuccess : OptionMenuUiEvent()

    data class RemindSuccess(
        val message: String,
        val startTime: Long?,
        val dueTime: Long?
    ) : OptionMenuUiEvent()



}