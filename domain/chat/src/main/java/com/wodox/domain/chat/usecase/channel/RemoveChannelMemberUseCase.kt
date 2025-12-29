package com.wodox.domain.chat.usecase.channel

import com.wodox.domain.base.BaseParamsUseCase
import com.wodox.domain.chat.repository.ChannelRepository
import java.util.UUID
import javax.inject.Inject

data class RemoveChannelMemberParams(
    val channelId: UUID,
    val userId: UUID
)

class RemoveChannelMemberUseCase @Inject constructor(
    private val channelRepository: ChannelRepository
) : BaseParamsUseCase<RemoveChannelMemberParams, Unit>() {
    override suspend fun execute(params: RemoveChannelMemberParams) {
        channelRepository.removeChannelMember(params.channelId, params.userId)
    }
}