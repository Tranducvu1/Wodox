package com.wodox.data.home.repository

import com.wodox.data.home.datasource.local.database.task.dao.LogDao
import com.wodox.data.home.datasource.local.database.task.mapper.LogMapper
import com.wodox.domain.home.model.local.Log
import com.wodox.domain.home.repository.LogRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Date
import java.util.UUID
import javax.inject.Inject

class LogRepositoryImpl @Inject constructor(
    private val dao: LogDao,
    private val mapper: LogMapper,
) : LogRepository {
    override suspend fun save(log: Log): Log? {
        val entity = mapper.mapToEntity(log).apply {
            this.updatedAt = Date()
        }
        dao.save(entity)
        return mapper.mapToDomain(entity)
    }

    override suspend fun getAllLog(taskId: UUID): Flow<List<Log>> {
        return dao.getLogByTaskId(taskId).map { entities ->
            mapper.mapToDomainList(entities)
        }
    }
}