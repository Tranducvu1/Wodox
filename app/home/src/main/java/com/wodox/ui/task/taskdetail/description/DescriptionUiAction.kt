package com.wodox.ui.task.taskdetail.description

import android.text.Editable

sealed class DescriptionUiAction {
    data class  SaveDescription(val description: String?) : DescriptionUiAction()
}