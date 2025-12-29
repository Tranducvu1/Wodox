package com.wodox.domain.chat.usecase

import com.wodox.domain.chat.model.local.Notification
import com.wodox.domain.chat.repository.NotificationRepository
import com.wodox.domain.base.BaseParamsUnsafeUseCase
import javax.inject.Inject

class SaveNotificationUseCase @Inject constructor(
    private val repository: NotificationRepository
) : BaseParamsUnsafeUseCase<Notification, Notification?>() {
    override suspend fun execute(params: Notification): Notification? {
        return repository.save(params)
    }
}
