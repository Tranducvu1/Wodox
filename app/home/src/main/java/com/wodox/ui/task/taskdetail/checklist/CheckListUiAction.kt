package com.wodox.ui.task.taskdetail.checklist

sealed class CheckListUiAction {
    data class AddNewDescription(val description:String) : CheckListUiAction()
}