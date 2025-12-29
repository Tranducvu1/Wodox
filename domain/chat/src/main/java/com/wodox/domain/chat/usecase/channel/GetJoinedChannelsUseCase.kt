package com.wodox.domain.chat.usecase.channel

import com.wodox.domain.base.BaseParamsFlowUnsafeUseCase
import com.wodox.domain.chat.model.Channel
import com.wodox.domain.chat.repository.ChannelRepository
import kotlinx.coroutines.flow.Flow
import java.util.UUID
import javax.inject.Inject

class GetJoinedChannelsUseCase @Inject constructor(
    private val channelRepository: ChannelRepository
) : BaseParamsFlowUnsafeUseCase<UUID, List<Channel>>() {
    override suspend fun execute(params: UUID): Flow<List<Channel>> {
        return channelRepository.getJoinedChannels(params)

    }
}