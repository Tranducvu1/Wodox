package com.wodox.domain.chat.usecase
import com.wodox.domain.base.BaseParamsUnsafeUseCase
import com.wodox.domain.base.BaseUseCase
import com.wodox.domain.chat.model.local.Notification
import com.wodox.domain.chat.model.local.NotificationActionType
import com.wodox.domain.chat.repository.NotificationRepository
import java.util.UUID

data class Params(
    val taskId: UUID,
    val taskName: String,
    val title: String,
    val description: String,
    val toUserId: UUID,
    val fromUserId: UUID,
    val fromUserName: String,
    val userAvatar: String,
    val endTime: Long,
    val notificationType: String
)

class SendTaskReminderUseCase(
    private val repository: NotificationRepository
) : BaseParamsUnsafeUseCase<Params, Notification?>() {
    override suspend fun execute(params: Params): Notification? {
        val notification = Notification(
            id = UUID.randomUUID(),
            userId = params.toUserId,
            content = "${params.title}: ${params.description}",
            fromUserId = params.fromUserId,
            fromUserName = params.fromUserName,
            userAvatar = params.userAvatar,
            taskId = params.taskId,
            taskName = params.taskName,
            actionType = NotificationActionType.DEADLINE_REMINDER,
            timestamp = params.endTime,
            isRead = false,
            isDismissed = false
        )
        return repository.save(notification)
    }
}
