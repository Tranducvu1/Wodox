package com.wodox.domain.home.usecase.log

import com.wodox.domain.base.BaseParamsUnsafeUseCase
import com.wodox.domain.home.model.local.Log
import com.wodox.domain.home.repository.LogRepository
import javax.inject.Inject

class SaveLogUseCase @Inject constructor(
    private val repository: LogRepository
) : BaseParamsUnsafeUseCase<Log, Log?>() {
    override suspend fun execute(params: Log): Log? {
        return repository.save(params)
    }
}