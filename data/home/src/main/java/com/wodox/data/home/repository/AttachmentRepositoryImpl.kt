package com.wodox.data.home.repository

import androidx.paging.PagingData
import com.wodox.data.home.datasource.local.database.task.dao.AttachmentDao
import com.wodox.data.home.datasource.local.database.task.mapper.AttachmentMapper
import com.wodox.domain.home.model.local.Attachment
import com.wodox.domain.home.repository.AttachmentRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Date
import java.util.UUID
import javax.inject.Inject

class AttachmentRepositoryImpl @Inject constructor(
    private val dao: AttachmentDao,
    private val mapper: AttachmentMapper
) : AttachmentRepository {
    override fun getAllTaskByUserID(taskId: UUID): Flow<List<Attachment>> {
        return dao.getAttachmentByTask(taskId).map { entities ->
            mapper.mapToDomainList(entities)
        }
    }
    override suspend fun save(attachment: Attachment): Attachment? {
        val entity = mapper.mapToEntity(attachment).apply {
            this.updatedAt = Date()
        }
        dao.save(entity)
        return mapper.mapToDomain(entity)
    }

}