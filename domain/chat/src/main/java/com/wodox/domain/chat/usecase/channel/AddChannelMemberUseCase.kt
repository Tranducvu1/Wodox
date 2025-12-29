package com.wodox.domain.chat.usecase.channel
import com.wodox.domain.base.BaseParamsUseCase
import com.wodox.domain.chat.model.ChannelMember
import com.wodox.domain.chat.repository.ChannelRepository
import javax.inject.Inject

class AddChannelMemberUseCase @Inject constructor(
    private val channelRepository: ChannelRepository
) : BaseParamsUseCase<ChannelMember, Unit>() {
    override suspend fun execute(params: ChannelMember) {
        channelRepository.addChannelMember(params)
    }
}