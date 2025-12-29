package com.wodox.data.home.repository


import com.wodox.data.home.datasource.local.database.task.dao.UserFriendDao
import com.wodox.data.home.datasource.local.database.task.mapper.UserFriendMapper
import com.wodox.domain.home.model.local.FriendStatus
import com.wodox.domain.home.model.local.Task
import com.wodox.domain.home.model.local.UserFriend
import com.wodox.domain.home.repository.UserFriendRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.*
import javax.inject.Inject

class UserFriendRepositoryImpl @Inject constructor(
    private val dao: UserFriendDao,
    private val mapper: UserFriendMapper
) : UserFriendRepository {

    override fun getAll(): Flow<List<UserFriend>> {
        return dao.getAll().map { list -> list.map { mapper.mapToDomain(it) } }
    }

    override fun getFriendSent(userId: UUID): Flow<List<UserFriend>> {
        return dao.getFriendSent(userId).map { list -> list.map { mapper.mapToDomain(it) } }
    }

    override fun getFriendRequests(userId: UUID): Flow<List<UserFriend>> {
        return dao.getReceivedRequests(userId).map { list -> list.map { mapper.mapToDomain(it) } }
    }

    override fun getAcceptedFriends(userId: UUID): Flow<List<UserFriend>> {
        return dao.getAcceptedFriends(userId).map { list -> list.map { mapper.mapToDomain(it) } }
    }

    override suspend fun getById(relationId: UUID): UserFriend? {
        return dao.getById(relationId)?.let { mapper.mapToDomain(it) }
    }

    override suspend fun save(userFriend: UserFriend): UserFriend {
        val entity = mapper.mapToEntity(userFriend).apply {
            updatedAt = Date()
        }
        dao.save(entity)
        return mapper.mapToDomain(entity)
    }

    override suspend fun deleteSoft(relationId: UUID): Int {
        return dao.softDelete(relationId)
    }

    override suspend fun updateStatus(relationId: UUID, status: FriendStatus): Int {
        val entity = dao.getById(relationId) ?: return 0
        val updated = entity.copy(status = status, updatedAt = Date())
        return dao.update(updated)
    }

    override suspend fun findRelation(userId: UUID, friendId: UUID): UserFriend? {
        return dao.findRelation(userId, friendId)?.let { mapper.mapToDomain(it) }
    }
}

