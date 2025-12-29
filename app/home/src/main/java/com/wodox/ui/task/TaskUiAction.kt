package com.wodox.ui.task

import com.wodox.ui.task.menu.TaskBarMenu

sealed class TaskUiAction {
    data class ChangeTab(val type: TaskBarMenu.TaskBarMenuType) : TaskUiAction()
    object HandleUpdateFavourite : TaskUiAction()
}