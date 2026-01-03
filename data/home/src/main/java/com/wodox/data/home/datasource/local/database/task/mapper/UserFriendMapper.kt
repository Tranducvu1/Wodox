package com.wodox.data.home.datasource.local.database.task.mapper

import com.wodox.data.home.datasource.local.database.task.entity.UserFriendEntity
import com.wodox.domain.home.model.local.FriendStatus
import com.wodox.domain.home.model.local.UserFriend
import java.util.UUID
import javax.inject.Inject

class UserFriendMapper @Inject constructor() {

    fun mapToDomain(entity: UserFriendEntity): UserFriend {
        return UserFriend(
            id = UUID.fromString(entity.id),
            userId = UUID.fromString(entity.userId),
            friendId = UUID.fromString(entity.friendId),
            status = FriendStatus.valueOf(entity.status),
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt,
            deletedAt = entity.deletedAt
        )
    }

    fun mapToEntity(domain: UserFriend): UserFriendEntity {
        return UserFriendEntity(
            id = domain.id.toString(),
            userId = domain.userId.toString(),
            friendId = domain.friendId.toString(),
            status = domain.status.name,
            createdAt = domain.createdAt,
            updatedAt = domain.updatedAt,
            deletedAt = domain.deletedAt
        )
    }

    fun mapToDomainList(entities: List<UserFriendEntity>): List<UserFriend> {
        return entities.map { mapToDomain(it) }
    }

    fun mapToEntityList(domains: List<UserFriend>): List<UserFriendEntity> {
        return domains.map { mapToEntity(it) }
    }
}