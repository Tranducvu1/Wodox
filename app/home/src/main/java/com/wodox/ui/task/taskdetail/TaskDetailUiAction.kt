package com.wodox.ui.task.taskdetail

import android.net.Uri
import com.wodox.domain.home.model.local.Attachment
import com.wodox.domain.home.model.local.TaskStatus
import com.wodox.domain.home.model.local.AttachmentType
import com.wodox.domain.home.model.local.SubTask
import com.wodox.ui.task.taskdetail.createtask.CreateTaskUiAction
import java.util.UUID

sealed class TaskDetailUiAction {
    object LoadAttachment : TaskDetailUiAction()
    data class UpdateTaskState(val status: TaskStatus) : TaskDetailUiAction()
    data class UpdateDay(val start: Long, val end: Long) : TaskDetailUiAction()

    data class UpdateAttachment(val uri: Uri, val type: AttachmentType, val taskID: UUID?) : TaskDetailUiAction()
    data class DeleteAttachment(val attachment: Attachment) : TaskDetailUiAction()
    data class UpdatePriority(val priority: Int) : TaskDetailUiAction()

    data class UpdateTitle(val title: String) : TaskDetailUiAction()

    data class DeleteSubTask(val subTask: SubTask) : TaskDetailUiAction()

    data class AssignUser(val id:UUID) : TaskDetailUiAction()

    object AssignSuccessfully :  TaskDetailUiAction()

    object AnalyzeUserSkill : TaskDetailUiAction()

    data class UpdateDifficulty(val difficulty: Int, val difficultyName: String) : TaskDetailUiAction()



}