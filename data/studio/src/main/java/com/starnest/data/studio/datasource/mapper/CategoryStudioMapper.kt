package com.starnest.data.studio.datasource.mapper

import com.starnest.data.base.AbstractMapper
import com.starnest.data.studio.datasource.entity.CategoryStudioEntity
import com.starnest.domain.studio.model.CategoryStudio
import javax.inject.Inject

class CategoryStudioMapper @Inject constructor() :
    AbstractMapper<CategoryStudioEntity, CategoryStudio>() {
    override fun mapToDomain(entity: CategoryStudioEntity): CategoryStudio {
        return CategoryStudio(
            id = entity.id,
            name = entity.name,
            isHidden = entity.isHidden,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt,
            deletedAt = entity.deletedAt
        )
    }

    override fun mapToEntity(domain: CategoryStudio): CategoryStudioEntity {
        return CategoryStudioEntity(
            id = domain.id,
            name = domain.name,
            isHidden = domain.isHidden,
            createdAt = domain.createdAt,
            updatedAt = domain.updatedAt,
            deletedAt = domain.deletedAt
        )
    }
}