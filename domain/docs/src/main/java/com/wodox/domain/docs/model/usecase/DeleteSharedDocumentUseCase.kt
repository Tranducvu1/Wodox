package com.wodox.domain.docs.model.usecase

import com.wodox.domain.base.BaseParamsUnsafeUseCase
import com.wodox.domain.docs.model.repository.SharedDocumentRepository
import javax.inject.Inject

class DeleteSharedDocumentUseCase @Inject constructor(
    private val repository: SharedDocumentRepository
) : BaseParamsUnsafeUseCase<String, Unit>() {
    override suspend fun execute(params: String) {
        repository.deleteSharedDocument(params)
    }
}
