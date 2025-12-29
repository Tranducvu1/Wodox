package com.starnest.data.studio.datasource.mapper

import com.starnest.data.base.AbstractMapper
import com.starnest.data.studio.datasource.entity.DrawEntity
import com.starnest.domain.studio.model.DrawItem
import javax.inject.Inject


class DrawingItemMapper @Inject constructor() :
    AbstractMapper<DrawEntity, DrawItem>() {
    override fun mapToDomain(entity: DrawEntity): DrawItem {
        return DrawItem(
            id = entity.id,
            rect = entity.rect,
            name = entity.name,
            isFavourite = entity.isFavourite,
            categoryId = entity.categoryId,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt,
            deletedAt = entity.deletedAt
        )
    }

    override fun mapToEntity(domain: DrawItem): DrawEntity {
        return DrawEntity(
            id = domain.id,
            rect = domain.rect,
            isFavourite = domain.isFavourite,
            categoryId = domain.categoryId,
            createdAt = domain.createdAt,
            updatedAt = domain.updatedAt,
            deletedAt = domain.deletedAt
        )
    }
}