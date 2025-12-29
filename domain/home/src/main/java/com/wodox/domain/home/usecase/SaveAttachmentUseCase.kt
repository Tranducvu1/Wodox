package com.wodox.domain.home.usecase

import com.wodox.domain.base.BaseParamsUnsafeUseCase
import com.wodox.domain.home.model.local.Attachment
import com.wodox.domain.home.repository.AttachmentRepository
import javax.inject.Inject

class SaveAttachmentUseCase @Inject constructor(
    private val repository: AttachmentRepository
) : BaseParamsUnsafeUseCase<Attachment, Attachment?>() {
    override suspend fun execute(params: Attachment): Attachment? {
        return repository.save(params)
    }
}
