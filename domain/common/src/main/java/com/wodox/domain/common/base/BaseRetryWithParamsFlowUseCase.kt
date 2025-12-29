package com.wodox.domain.common.base

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.retryWhen

abstract class BaseRetryWithParamsFlowUseCase<in Params, out ResultType> : BaseUseCase() {
    abstract val retries: Int
    abstract val delay: Long

    abstract suspend fun execute(params: Params): Flow<ResultType>

    operator fun invoke(params: Params): Flow<Result<ResultType>> = flow {
        emit(Result.Loading)
        try {
            execute(params)
                .collect { result ->
                    emit(Result.Success(result))
                }
        } catch (e: Exception) {
            emit(Result.Error(handleError(e)))
        }
    }
        .retryWhen { cause, attempt ->
            if (cause is NetworkError && attempt < retries) {
                delay(delay)
                true
            } else {
                false
            }
        }
        .flowOn(Dispatchers.IO)
}