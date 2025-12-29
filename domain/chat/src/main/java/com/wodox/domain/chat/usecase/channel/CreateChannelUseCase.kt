package com.wodox.domain.chat.usecase.channel

import com.wodox.domain.base.BaseParamsUnsafeUseCase
import com.wodox.domain.chat.model.Channel
import com.wodox.domain.chat.repository.ChannelRepository
import javax.inject.Inject

class CreateChannelUseCase @Inject constructor(
    private val channelRepository: ChannelRepository
) : BaseParamsUnsafeUseCase<Channel, Channel>() {
    override suspend fun execute(params: Channel): Channel {
        return channelRepository.createChannel(params)
    }
}