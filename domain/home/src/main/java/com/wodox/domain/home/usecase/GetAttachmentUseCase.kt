package com.wodox.domain.home.usecase

import com.wodox.domain.base.BaseParamsFlowUnsafeUseCase
import com.wodox.domain.home.model.local.Attachment
import com.wodox.domain.home.repository.AttachmentRepository
import kotlinx.coroutines.flow.Flow
import java.util.UUID

class GetAttachmentUseCase(
    private val repository: AttachmentRepository
) : BaseParamsFlowUnsafeUseCase<UUID,List<Attachment>>() {
    override suspend fun execute(taskId: UUID): Flow<List<Attachment>> {
        return repository.getAllTaskByUserID(taskId)
    }
}