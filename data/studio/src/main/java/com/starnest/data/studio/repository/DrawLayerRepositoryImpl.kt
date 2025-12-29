package com.starnest.data.studio.repository

import com.starnest.data.studio.datasource.dao.DrawLayerDao
import com.starnest.data.studio.datasource.mapper.DrawingLayerMapper
import com.starnest.domain.studio.model.DrawLayer
import com.starnest.domain.studio.repository.DrawLayerRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Date
import java.util.UUID


class DrawLayerRepositoryImpl(
    private val dao: DrawLayerDao,
    private val mapper: DrawingLayerMapper,
) : DrawLayerRepository {
    override suspend fun save(drawLayer: DrawLayer): DrawLayer {
        val entity = mapper.mapToEntity(drawLayer).apply {
            this.updatedAt = Date()
        }
        dao.save(entity)
        return mapper.mapToDomain(entity)
    }

    override suspend fun getDrawLayersByDrawId(drawId: UUID): Flow<List<DrawLayer>> {
        return dao.getDrawLayersByDrawId(drawId)
            .map { entities ->
                mapper.mapToDomainList(entities) }
    }
}