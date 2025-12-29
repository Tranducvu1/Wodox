package com.wodox.domain.docs.model.usecase

import com.wodox.domain.base.BaseParamsUnsafeUseCase
import com.wodox.domain.docs.model.model.SharedDocument
import com.wodox.domain.docs.model.repository.SharedDocumentRepository
import javax.inject.Inject

class GetSharedDocumentByIdUseCase @Inject constructor(
    private val repository: SharedDocumentRepository
) : BaseParamsUnsafeUseCase<String, SharedDocument?>() {
    override suspend fun execute(params: String): SharedDocument? {
        return repository.getSharedDocumentById(params)
    }
}
