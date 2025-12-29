package com.wodox.data.home.datasource.local.database.task.mapper


import com.wodox.base.base.AbstractMapper
import com.wodox.data.home.datasource.local.database.task.entity.UserFriendEntity
import com.wodox.domain.home.model.local.UserFriend
import javax.inject.Inject

class UserFriendMapper @Inject constructor() :
    AbstractMapper<UserFriendEntity, UserFriend>() {

    override fun mapToDomain(entity: UserFriendEntity): UserFriend {
        return UserFriend(
            id = entity.id,
            userId = entity.userId,
            friendId = entity.friendId,
            status = entity.status,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt,
            deletedAt = entity.deletedAt
        )
    }

    override fun mapToEntity(domain: UserFriend): UserFriendEntity {
        return UserFriendEntity(
            id = domain.id,
            userId = domain.userId,
            friendId = domain.friendId,
            status = domain.status,
            createdAt = domain.createdAt,
            updatedAt = domain.updatedAt,
            deletedAt = domain.deletedAt
        )
    }
}
