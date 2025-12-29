package com.wodox.domain.chat.usecase.channel

import com.wodox.domain.base.BaseParamsFlowUnsafeUseCase
import com.wodox.domain.chat.model.ChannelMessage
import com.wodox.domain.chat.repository.ChannelRepository
import kotlinx.coroutines.flow.Flow
import java.util.UUID
import javax.inject.Inject

class GetChannelMessagesUseCase @Inject constructor(
    private val channelRepository: ChannelRepository
) : BaseParamsFlowUnsafeUseCase<UUID, List<ChannelMessage>>() {
    override suspend fun execute(params: UUID): Flow<List<ChannelMessage>> {
        return channelRepository.getChannelMessages(params)
    }
}