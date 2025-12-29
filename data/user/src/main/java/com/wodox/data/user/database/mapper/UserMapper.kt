package com.wodox.data.user.database.mapper

import com.wodox.base.base.AbstractMapper
import com.wodox.data.user.database.entity.UserEntity
import com.wodox.domain.user.model.User
import java.util.UUID
import javax.inject.Inject

class UserMapper @Inject constructor() : AbstractMapper<UserEntity, User>() {
    override fun mapToDomain(entity: UserEntity): User {
        return User(
            id = UUID.fromString(entity.id),
            email = entity.email,
            password = entity.password,
            name = entity.name,
            avatar = entity.avatar,
            isActive = entity.isActive,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt,
            deletedAt = entity.deletedAt
        )
    }

    override fun mapToEntity(domain: User): UserEntity {
        return UserEntity(
            id = domain.id.toString(),
            email = domain.email,
            password = domain.password,
            name = domain.name,
            avatar = domain.avatar,
            isActive = domain.isActive,
            createdAt = domain.createdAt,
            updatedAt = domain.updatedAt,
            deletedAt = domain.deletedAt
        )
    }

    fun mapToMap(entity: UserEntity): Map<String, Any?> {
        return mapOf(
            "id" to entity.id.toString(),
            "email" to entity.email,
            "name" to entity.name,
            "avatar" to entity.avatar,
            "isActive" to entity.isActive,
            "createdAt" to entity.createdAt,
            "updatedAt" to entity.updatedAt,
            "deletedAt" to entity.deletedAt
        )
    }

    fun mapUserToMap(user: User): Map<String, Any?> {
        return mapToMap(mapToEntity(user))
    }
}
