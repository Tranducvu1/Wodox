package com.wodox.domain.chat.usecase.channel

import com.wodox.domain.base.BaseNoParamsFlowUnsafeUseCase
import com.wodox.domain.chat.model.Channel
import com.wodox.domain.chat.repository.ChannelRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllChannelsUseCase @Inject constructor(
    private val channelRepository: ChannelRepository
) : BaseNoParamsFlowUnsafeUseCase<List<Channel>>() {
    override suspend fun execute(): Flow<List<Channel>> {
        return channelRepository.getAllChannels()
    }
}