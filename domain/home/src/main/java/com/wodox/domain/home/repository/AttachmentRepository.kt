package com.wodox.domain.home.repository

import androidx.paging.PagingData
import com.wodox.domain.home.model.local.Attachment
import kotlinx.coroutines.flow.Flow
import java.util.UUID

interface AttachmentRepository {
    fun getAllTaskByUserID(taskId: UUID): Flow<List<Attachment>>
    suspend fun save(attachment: Attachment): Attachment?
}