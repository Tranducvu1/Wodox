package com.wodox.mywork.ui

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.wodox.core.base.viewmodel.BaseUiStateViewModel
import com.wodox.domain.home.model.local.Comment
import com.wodox.domain.home.model.local.Task
import com.wodox.domain.home.usecase.GetTaskUseCase
import com.wodox.domain.home.usecase.comment.GetLatestUnreadCommentUseCase
import com.wodox.domain.user.usecase.GetUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject


@HiltViewModel
class MyWorkViewModel @Inject constructor(
    val app: Application,
    private val getUserUseCase: GetUserUseCase,
    private val getTaskUseCase: GetTaskUseCase,
    private val getLatestUnreadCommentUseCase: GetLatestUnreadCommentUseCase
) : BaseUiStateViewModel<MyWorkUiState, MyWorkUiEvent, MyWorkUiAction>(app) {
    override fun initialState(): MyWorkUiState = MyWorkUiState()

    val latestComment: StateFlow<Comment?> = flow {
        val userId = getUserUseCase()
        if (userId != null) {
            getLatestUnreadCommentUseCase(userId).collect { emit(it) }
        } else {
            emit(null)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    val allTasks: StateFlow<List<Task>> = getTaskUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = emptyList()
        )

}