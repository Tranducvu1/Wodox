package com.wodox.domain.chat.usecase.channel

import com.wodox.domain.base.BaseParamsUseCase
import com.wodox.domain.chat.repository.ChannelRepository
import java.util.UUID
import javax.inject.Inject

class ClearUnreadCountUseCase @Inject constructor(
    private val channelRepository: ChannelRepository
) : BaseParamsUseCase<UUID, Unit>() {
    override suspend fun execute(params: UUID) {
        channelRepository.clearUnreadCount(params)
    }
}