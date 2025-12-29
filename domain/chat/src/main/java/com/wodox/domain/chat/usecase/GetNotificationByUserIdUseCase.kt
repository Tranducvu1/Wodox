package com.wodox.domain.chat.usecase

import com.wodox.domain.chat.model.local.Notification
import com.wodox.domain.chat.repository.NotificationRepository
import com.wodox.domain.base.BaseParamsFlowUnsafeUseCase
import kotlinx.coroutines.flow.Flow
import java.util.UUID
import javax.inject.Inject

class GetNotificationByUserIdUseCase @Inject constructor(
    private val notificationRepository: NotificationRepository,
) : BaseParamsFlowUnsafeUseCase<UUID, List<Notification>>() {
    override suspend fun execute(params: UUID): Flow<List<Notification>> {
        return notificationRepository.getNotificationByUserId(params)
    }
}