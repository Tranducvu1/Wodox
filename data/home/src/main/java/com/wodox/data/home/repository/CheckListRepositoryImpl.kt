package com.wodox.data.home.repository

import com.wodox.data.home.datasource.local.database.task.dao.CheckListDao
import com.wodox.data.home.datasource.local.database.task.dao.SubTaskDao
import com.wodox.data.home.datasource.local.database.task.mapper.CheckListMapper
import com.wodox.data.home.datasource.local.database.task.mapper.SubTaskMapper
import com.wodox.domain.home.model.local.CheckList
import com.wodox.domain.home.model.local.SubTask
import com.wodox.domain.home.repository.CheckListRepository
import com.wodox.domain.home.repository.SubTaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Date
import java.util.UUID
import javax.inject.Inject


class CheckListRepositoryImpl @Inject constructor(
    private val dao: CheckListDao,
    private val mapper: CheckListMapper
) : CheckListRepository {
    override fun getAllCheckListByTaskID(id: UUID): Flow<List<CheckList>> {
        return dao.getAllCheckList(id).map { list ->
            list.map { entity ->
                mapper.mapToDomain(entity)
            }
        }
    }

    override suspend fun save(checkList: CheckList): CheckList? {
        val entity = mapper.mapToEntity(checkList).apply {
            this.updatedAt = Date()
        }
        dao.save(entity)
        return mapper.mapToDomain(entity)
    }
}