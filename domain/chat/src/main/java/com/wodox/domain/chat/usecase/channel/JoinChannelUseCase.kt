package com.wodox.domain.chat.usecase.channel


import com.wodox.domain.base.BaseParamsUseCase
import com.wodox.domain.chat.model.ChannelMember
import com.wodox.domain.chat.model.ChannelRole
import com.wodox.domain.chat.repository.ChannelRepository
import java.util.UUID
import javax.inject.Inject

data class JoinChannelParams(
    val channelId: UUID,
    val userId: UUID
)

class JoinChannelUseCase @Inject constructor(
    private val channelRepository: ChannelRepository
) : BaseParamsUseCase<JoinChannelParams, Unit>() {
    override suspend fun execute(params: JoinChannelParams) {
        val member = ChannelMember(
            channelId = params.channelId,
            userId = params.userId,
            role = ChannelRole.MEMBER
        )
        channelRepository.addChannelMember(member)
    }
}