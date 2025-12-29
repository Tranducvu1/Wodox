package com.wodox.domain.docs.model.usecase

import com.wodox.domain.base.BaseParamsUnsafeUseCase
import com.wodox.domain.docs.model.model.SharedDocument
import com.wodox.domain.docs.model.repository.SharedDocumentRepository
import javax.inject.Inject

class SaveSharedDocumentUseCase @Inject constructor(
    private val repository: SharedDocumentRepository
) : BaseParamsUnsafeUseCase<SharedDocument, Unit>() {
    override suspend fun execute(params: SharedDocument) {
        repository.saveSharedDocument(params)
    }
}