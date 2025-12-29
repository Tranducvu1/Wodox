package com.wodox.ui.task.taskdetail.createtask

import com.wodox.domain.home.model.local.Attachment
import com.wodox.domain.home.model.local.Difficulty
import com.wodox.domain.home.model.local.Priority
import com.wodox.domain.home.model.local.Task
import com.wodox.ui.task.taskdetail.TaskDetailUiAction
import java.util.UUID

sealed class CreateTaskUiAction {
    data class SaveTask(
        val task: Task
    ) : CreateTaskUiAction()
    data class DeleteAttachment(val attachment: Attachment) : CreateTaskUiAction()

    data class LoadSuggestedSupporters(
        val difficulty: Difficulty,
        val priority: Priority
    ) : CreateTaskUiAction()
    object AnalyzeUserSkill : CreateTaskUiAction()

    data class AssignUser(val id : UUID)  : CreateTaskUiAction()

    data class UpdateDifficulty(val difficulty: Int, val difficultyName: String) : CreateTaskUiAction()

}