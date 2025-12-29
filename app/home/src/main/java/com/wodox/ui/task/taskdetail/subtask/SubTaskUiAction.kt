package com.wodox.ui.task.taskdetail.subtask

import com.wodox.domain.home.model.local.Attachment
import com.wodox.domain.home.model.local.SubTask
import com.wodox.ui.task.taskdetail.TaskDetailUiAction
import java.util.UUID

sealed class SubTaskUiAction {
    data class SaveSubTask(
        val subtask: SubTask
    ) : SubTaskUiAction()
    data class DeleteAttachment(val attachment: Attachment) : SubTaskUiAction()

}