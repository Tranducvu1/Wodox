package com.wodox.domain.common.base

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

abstract class BaseNoParamsFlowUseCase<out ResultType> : BaseUseCase() {
    abstract suspend fun execute(): Flow<ResultType>

    operator fun invoke(): Flow<Result<ResultType>> = flow {
        emit(Result.Loading)
        try {
            execute()
                .collect { result ->
                    emit(Result.Success(result))
                }
        } catch (e: Exception) {
            emit(Result.Error(handleError(e)))
        }
    }
        .flowOn(Dispatchers.IO)
} 