package com.wodox.domain.common.base

abstract class BaseNoParamsUseCase<out ResultType>: BaseUseCase() {
    
    abstract suspend fun execute(): ResultType

    suspend operator fun invoke(): Result<ResultType> {
        return try {
            val result = withIOContext { execute() }
            Result.Success(result)
        } catch (e: Exception) {
            Result.Error(handleError(e))
        }
    }
} 