package com.wodox.domain.chat.usecase.channel


import com.wodox.domain.base.BaseParamsFlowUnsafeUseCase
import com.wodox.domain.chat.model.ChannelMember
import com.wodox.domain.chat.repository.ChannelRepository
import kotlinx.coroutines.flow.Flow
import java.util.UUID
import javax.inject.Inject

class GetChannelMembersUseCase @Inject constructor(
    private val channelRepository: ChannelRepository
) : BaseParamsFlowUnsafeUseCase<UUID, List<ChannelMember>>() {
    override suspend fun execute(params: UUID): Flow<List<ChannelMember>> {
        return channelRepository.getChannelMembers(params)
    }
}