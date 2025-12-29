package com.wodox.domain.home.usecase


import com.wodox.domain.base.BaseParamsUnsafeUseCase
import com.wodox.domain.home.model.local.UserFriend
import com.wodox.domain.home.repository.UserFriendRepository
import java.util.UUID
import javax.inject.Inject


data class Params(
    val userId: UUID,
    val friendId: UUID
)

class FindRelationUseCase @Inject constructor(
    private val repository: UserFriendRepository
) : BaseParamsUnsafeUseCase<Params, UserFriend?>() {
    override suspend fun execute(params: Params): UserFriend? {
        return repository.findRelation(params.userId, params.friendId)
    }
}
