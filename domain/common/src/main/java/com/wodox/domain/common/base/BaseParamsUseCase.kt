package com.wodox.domain.common.base

abstract class BaseParamsUseCase<in Params, out ResultType>: BaseUseCase() {
    
    abstract suspend fun execute(params: Params): ResultType

    suspend operator fun invoke(params: Params): Result<ResultType> {
        return try {
            val result = withIOContext { execute(params) }
            Result.Success(result)
        } catch (e: Exception) {
            Result.Error(handleError(e))
        }
    }
}
