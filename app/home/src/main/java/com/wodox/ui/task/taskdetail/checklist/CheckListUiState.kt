package com.wodox.ui.task.taskdetail.checklist

import com.wodox.domain.home.model.local.CheckList

data class CheckListUiState (
    val checkList : List<CheckList> = emptyList()
)