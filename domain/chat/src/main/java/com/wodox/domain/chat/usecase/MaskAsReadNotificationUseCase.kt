package com.wodox.domain.chat.usecase

import com.wodox.domain.chat.repository.NotificationRepository
import com.wodox.domain.base.BaseParamsUnsafeUseCase
import java.util.UUID
import javax.inject.Inject

class MarkAsReadNotificationUseCase @Inject constructor(
    private val userRepository: NotificationRepository
) : BaseParamsUnsafeUseCase<UUID, Unit?>() {
    override suspend fun execute(params: UUID) {
        userRepository.markAsRead(params)
    }
}

