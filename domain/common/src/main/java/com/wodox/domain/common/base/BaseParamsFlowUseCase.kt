package com.wodox.domain.common.base

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

abstract class BaseParamsFlowUseCase<in Params, out ResultType>: BaseUseCase() {

    abstract suspend fun execute(params: Params): Flow<ResultType>

    operator fun invoke(params: Params): Flow<Result<ResultType>> = flow {
        emit(Result.Loading)
        try {
            execute(params = params)
                .collect { result ->
                    emit(Result.Success(result))
                }
        } catch (e: Exception) {
            emit(Result.Error(handleError(e)))
        }
    }
    .flowOn(Dispatchers.IO)
}
