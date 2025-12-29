package com.starnest.data.studio.datasource.mapper

import com.starnest.data.base.AbstractMapper
import com.starnest.data.studio.datasource.entity.DrawLayerEntity
import com.starnest.domain.studio.model.DrawLayer
import javax.inject.Inject

class DrawingLayerMapper @Inject constructor() :
    AbstractMapper<DrawLayerEntity, DrawLayer>() {
    override fun mapToDomain(entity: DrawLayerEntity): DrawLayer {
        return DrawLayer(
            id = entity.id,
            drawId = entity.drawId,
            isHidden = entity.isHidden,
            rect = entity.rect,
            name = entity.name,
            background = entity.background,
            snapshot = entity.snapshot,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt,
            deletedAt = entity.deletedAt
        )
    }

    override fun mapToEntity(domain: DrawLayer): DrawLayerEntity {
        return DrawLayerEntity(
            id = domain.id,
            drawId = domain.drawId,
            isHidden = domain.isHidden,
            rect = domain.rect,
            name = domain.name,
            background = domain.background,
            snapshot = domain.snapshot,
            createdAt = domain.createdAt,
            updatedAt = domain.updatedAt,
            deletedAt = domain.deletedAt
        )
    }
}