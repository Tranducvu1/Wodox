package com.wodox.domain.chat.usecase.channel

import com.wodox.domain.base.BaseParamsUnsafeUseCase
import com.wodox.domain.chat.model.Channel
import com.wodox.domain.chat.repository.ChannelRepository
import java.util.UUID
import javax.inject.Inject

class GetChannelByIdUseCase @Inject constructor(
    private val channelRepository: ChannelRepository
) : BaseParamsUnsafeUseCase<UUID, Channel?>() {
    override suspend fun execute(params: UUID): Channel? {
        return channelRepository.getChannelById(params)
    }
}