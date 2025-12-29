package com.wodox.ui.task.menuoption


sealed class OptionMenuUiAction {
    object DeleteTask : OptionMenuUiAction()

    object DuplicateTask : OptionMenuUiAction()

    object RemindTask : OptionMenuUiAction()


}