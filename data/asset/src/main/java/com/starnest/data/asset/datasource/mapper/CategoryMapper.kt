package com.starnest.data.asset.datasource.mapper

import com.starnest.data.asset.datasource.model.CategoryDto
import com.wodox.data.common.base.AbstractMapper
import com.wodox.domain.asset.model.Category

class CategoryMapper : AbstractMapper<CategoryDto, Category>() {
    override fun mapToDomain(dto: CategoryDto): Category {
        return Category(
            id = dto.id,
            name = dto.name,
            icon = dto.icon,
            isSelected = dto.isSelected,
        )
    }

    override fun mapToEntity(domain: Category): CategoryDto {
        return CategoryDto(
            id = domain.id,
            name = domain.name,
            icon = domain.icon,
            isSelected = domain.isSelected,
        )
    }
}