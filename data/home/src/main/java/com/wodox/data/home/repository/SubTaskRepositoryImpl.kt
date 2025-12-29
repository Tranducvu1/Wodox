package com.wodox.data.home.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.wodox.data.home.datasource.local.database.task.dao.SubTaskDao
import com.wodox.data.home.datasource.local.database.task.mapper.SubTaskMapper
import com.wodox.domain.home.model.local.SubTask
import com.wodox.domain.home.repository.SubTaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Date
import java.util.UUID
import javax.inject.Inject

class SubTaskRepositoryImpl @Inject constructor(
    private val dao: SubTaskDao,
    private val mapper: SubTaskMapper
) : SubTaskRepository {
    override fun getAllSubTaskByTaskID(id: UUID): Flow<List<SubTask>> {
        return dao.getSubTasksByTaskId(id).map { list ->
            list.map { entity ->
                mapper.mapToDomain(entity)
            }
        }
    }


    override suspend fun save(task: SubTask): SubTask? {
        val entity = mapper.mapToEntity(task).apply {
            this.updatedAt = Date()
        }
        dao.save(entity)
        return mapper.mapToDomain(entity)
    }
}