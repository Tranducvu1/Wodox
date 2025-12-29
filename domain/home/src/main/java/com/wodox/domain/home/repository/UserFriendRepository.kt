package com.wodox.domain.home.repository


import com.wodox.domain.home.model.local.FriendStatus
import com.wodox.domain.home.model.local.UserFriend
import kotlinx.coroutines.flow.Flow
import java.util.UUID

interface UserFriendRepository {

    fun getAll(): Flow<List<UserFriend>>

    fun getFriendSent(userId: UUID): Flow<List<UserFriend>>

    fun getFriendRequests(userId: UUID): Flow<List<UserFriend>>

    fun getAcceptedFriends(userId: UUID): Flow<List<UserFriend>>

    suspend fun getById(relationId: UUID): UserFriend?

    suspend fun save(userFriend: UserFriend): UserFriend

    suspend fun deleteSoft(relationId: UUID): Int

    suspend fun updateStatus(
        relationId: UUID,
        status: FriendStatus
    ): Int

    suspend fun findRelation(userId: UUID, friendId: UUID): UserFriend?

}
