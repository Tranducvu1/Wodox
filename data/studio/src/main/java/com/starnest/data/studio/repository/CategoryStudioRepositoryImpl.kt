package com.starnest.data.studio.repository

import com.starnest.data.studio.datasource.dao.CategoryStudioDao
import com.starnest.data.studio.datasource.mapper.CategoryStudioMapper
import com.starnest.domain.studio.model.CategoryStudio
import com.starnest.domain.studio.repository.CategoryStudioRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Date

class CategoryStudioRepositoryImpl(
    val dao: CategoryStudioDao,
    private val mapper: CategoryStudioMapper
) : CategoryStudioRepository {
    override fun getAllCategoryStudio(): Flow<List<CategoryStudio>> {
        return dao.getAllCategory().map { entities ->
            mapper.mapToDomainList(entities)
        }
    }

    override suspend fun save(category: CategoryStudio): CategoryStudio? {
        val entity = mapper.mapToEntity(category).apply {
            this.updatedAt = Date()
        }
        dao.save(entity)
        return mapper.mapToDomain(entity)
    }

    override fun getAllShowCategoryStudio(): Flow<List<CategoryStudio>> {
        return dao.getAllShowCategory().map { entities ->
            mapper.mapToDomainList(entities)
        }
    }

}