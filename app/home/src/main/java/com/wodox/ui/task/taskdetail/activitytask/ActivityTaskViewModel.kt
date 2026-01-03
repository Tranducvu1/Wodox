package com.wodox.ui.task.taskdetail.activitytask

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.wodox.core.base.viewmodel.BaseUiStateViewModel
import com.wodox.core.extension.serializable
import com.wodox.domain.home.model.local.Comment
import com.wodox.domain.home.model.local.Task
import com.wodox.domain.home.usecase.log.GetAllLogUseCase
import com.wodox.domain.home.usecase.comment.DeleteCommentUseCase
import com.wodox.domain.home.usecase.comment.GetAllCommentByTaskIdUseCase
import com.wodox.domain.home.usecase.comment.SaveCommentUseCase
import com.wodox.domain.home.usecase.comment.UpdateCommentParams
import com.wodox.domain.home.usecase.comment.UpdateCommentUseCase
import com.wodox.domain.user.usecase.GetCurrentUserEmail
import com.wodox.domain.user.usecase.GetUserUseCase
import com.wodox.model.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Date
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class ActivityTaskViewModel @Inject constructor(
    val app: Application,
    private val getAllLogUseCase: GetAllLogUseCase,
    private val getAllCommentByTaskIdUseCase: GetAllCommentByTaskIdUseCase,
    private val saveCommentUseCase: SaveCommentUseCase,
    private val deleteCommentUseCase: DeleteCommentUseCase,
    private val getCurrentUserEmail: GetCurrentUserEmail,
    private val getUserUseCase: GetUserUseCase,
    private val updateCommentUseCase: UpdateCommentUseCase,
) : BaseUiStateViewModel<ActivityTaskUiState, ActivityTaskUiEvent, ActivityTaskUiAction>(app) {

    override fun initialState(): ActivityTaskUiState = ActivityTaskUiState()

    val task by lazy {
        data?.serializable<Task>(Constants.Intents.TASK)
    }

    override fun onCreate() {
        super.onCreate()
        loadListLogItem()
        loadComments()
    }

    override fun handleAction(action: ActivityTaskUiAction) {
        super.handleAction(action)
        when (action) {
            ActivityTaskUiAction.LoadActivity -> loadListLogItem()
            ActivityTaskUiAction.LoadComments -> loadComments()
            is ActivityTaskUiAction.SendComment -> sendComment(action.content)
            is ActivityTaskUiAction.DeleteComment -> deleteComment(action.commentId)
            is ActivityTaskUiAction.UpdateComment -> updateComment(action.commentId, action.content)
            is ActivityTaskUiAction.StartEditComment -> startEditComment(action.comment)
            ActivityTaskUiAction.CancelEditComment -> cancelEditComment()
        }
    }

    private fun loadListLogItem() {
        viewModelScope.launch(Dispatchers.IO) {
            val taskId = task?.id ?: return@launch
            getAllLogUseCase(taskId).collect { listLog ->
                updateState { it.copy(listLogItem = listLog) }
            }
        }
    }

    private fun loadComments() {
        viewModelScope.launch(Dispatchers.IO) {
            val taskId = task?.id ?: return@launch
            updateState { it.copy(isLoadingComments = true) }
            try {
                getAllCommentByTaskIdUseCase(taskId).collect { comments ->
                    updateState {
                        it.copy(
                            listComments = comments,
                            isLoadingComments = false,
                            errorMessage = null
                        )
                    }
                }
            } catch (e: Exception) {
                updateState {
                    it.copy(
                        isLoadingComments = false,
                        errorMessage = e.message
                    )
                }
                sendEvent(ActivityTaskUiEvent.ShowError(e.message ?: "Unknown error"))
            }
        }
    }

    private fun sendComment(content: String) {
        if (content.isBlank()) {
            sendEvent(ActivityTaskUiEvent.ShowError("Comment cannot be empty"))
            return
        }
        viewModelScope.launch(Dispatchers.IO) {
            val email = getCurrentUserEmail()
            val userID = getUserUseCase()
            updateState {
                it.copy(
                    email = email,
                    userId = userID
                )
            }

            val taskId = task?.id ?: return@launch
            val userId = uiState.value.userId
            val userName = uiState.value.email

            val comment = Comment(
                id = UUID.randomUUID(),
                taskId = taskId,
                content = content,
                userId = userId,
                userName = userName,
                createdAt = Date(),
                updatedAt = Date(),
                deletedAt = null
            )

            val savedComment = saveCommentUseCase(comment)
            if (savedComment != null) {
                sendEvent(ActivityTaskUiEvent.CommentSentSuccess)
                loadComments()
            } else {
                sendEvent(ActivityTaskUiEvent.ShowError("Failed to save comment"))
            }
        }
    }

    private fun updateComment(commentId: UUID, newContent: String) {
        if (newContent.isBlank()) {
            sendEvent(ActivityTaskUiEvent.ShowError("Comment cannot be empty"))
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
                val currentComment = uiState.value.listComments.find { it.id == commentId }
                if (currentComment == null) {
                    sendEvent(ActivityTaskUiEvent.ShowError("Comment not found"))
                    return@launch
                }

                val updateParams = UpdateCommentParams(
                    commentId = commentId,
                    newContent = newContent
                )

                val result = updateCommentUseCase(updateParams)
                if (result) {
                    sendEvent(ActivityTaskUiEvent.CommentUpdateSuccess)
                    cancelEditComment()
                    loadComments()
                } else {
                    sendEvent(ActivityTaskUiEvent.ShowError("Failed to update comment"))
                }
        }
    }

    private fun deleteComment(commentId: UUID) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                deleteCommentUseCase(commentId)
                sendEvent(ActivityTaskUiEvent.CommentDeleteSuccess)
                loadComments()
            } catch (e: Exception) {
                sendEvent(ActivityTaskUiEvent.ShowError(e.message ?: "Error deleting comment"))
            }
        }
    }

    private fun startEditComment(comment: Comment) {
        updateState {
            it.copy(
                editingCommentId = comment.id,
                editingCommentContent = comment.content
            )
        }
        sendEvent(ActivityTaskUiEvent.StartEditMode(comment))
    }

    private fun cancelEditComment() {
        updateState {
            it.copy(
                editingCommentId = null,
                editingCommentContent = null
            )
        }
        sendEvent(ActivityTaskUiEvent.CancelEditMode)
    }
}