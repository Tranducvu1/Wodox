package com.wodox.domain.home.usecase.log

import com.wodox.domain.base.BaseParamsUnsafeUseCase
import com.wodox.domain.home.repository.LogRepository
import java.util.UUID
import javax.inject.Inject

class DeleteAllLogsByTaskIdUseCase @Inject constructor(
    private val repository: LogRepository
) : BaseParamsUnsafeUseCase<UUID, Boolean>() {
    override suspend fun execute(params: UUID): Boolean {
        return repository.deleteAllLogsByTaskIdFromFirestore(params)
    }
}