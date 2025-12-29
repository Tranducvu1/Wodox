package com.wodox.domain.common.base

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

abstract class BaseNoParamsFlowUnsafeUseCase<out ResultType> : BaseUseCase() {
    abstract suspend fun execute(): Flow<ResultType>

    operator fun invoke(): Flow<ResultType> = flow {
        execute()
            .collect { result ->
                emit(result)
            }
    }
        .flowOn(Dispatchers.IO)
} 