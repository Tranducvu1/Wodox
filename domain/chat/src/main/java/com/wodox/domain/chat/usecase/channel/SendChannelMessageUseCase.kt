package com.wodox.domain.chat.usecase.channel


import com.wodox.domain.base.BaseParamsUseCase
import com.wodox.domain.chat.model.ChannelMessage
import com.wodox.domain.chat.repository.ChannelRepository
import javax.inject.Inject

class SendChannelMessageUseCase @Inject constructor(
    private val channelRepository: ChannelRepository
) : BaseParamsUseCase<ChannelMessage, ChannelMessage>() {
    override suspend fun execute(params: ChannelMessage): ChannelMessage {
        return channelRepository.sendChannelMessage(params)
    }
}