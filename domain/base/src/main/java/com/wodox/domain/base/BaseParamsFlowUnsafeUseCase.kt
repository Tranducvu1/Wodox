package com.wodox.domain.base

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

abstract class BaseParamsFlowUnsafeUseCase<in Params, out ResultType> : BaseUseCase() {
    abstract suspend fun execute(params: Params): Flow<ResultType>

    operator fun invoke(params: Params): Flow<ResultType> = flow {
        execute(params = params)
            .collect { result ->
                emit(result)
            }
    }
        .flowOn(Dispatchers.IO)
}
