package com.wodox.domain.home.usecase

import com.wodox.domain.base.BaseParamsFlowUnsafeUseCase
import com.wodox.domain.home.model.local.UserFriend
import com.wodox.domain.home.repository.UserFriendRepository
import kotlinx.coroutines.flow.Flow
import java.util.UUID
import javax.inject.Inject

class GetFriendSentUseCase @Inject constructor(
    private val userFriendRepository: UserFriendRepository,
) : BaseParamsFlowUnsafeUseCase<UUID, List<UserFriend>>() {
    override suspend fun execute(params: UUID): Flow<List<UserFriend>> {
        return userFriendRepository.getFriendSent(params)
    }
}