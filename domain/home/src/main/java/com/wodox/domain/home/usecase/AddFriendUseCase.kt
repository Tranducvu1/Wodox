package com.wodox.domain.home.usecase

import com.wodox.domain.base.BaseParamsUnsafeUseCase
import com.wodox.domain.home.model.local.UserFriend
import com.wodox.domain.home.repository.UserFriendRepository
import javax.inject.Inject


class AddFriendUseCase @Inject constructor(
    private val repository: UserFriendRepository
) : BaseParamsUnsafeUseCase<UserFriend, UserFriend?>() {
    override suspend fun execute(params: UserFriend): UserFriend? {
        return repository.save(params)
    }
}