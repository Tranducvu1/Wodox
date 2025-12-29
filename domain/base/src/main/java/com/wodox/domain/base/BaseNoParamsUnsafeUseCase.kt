package com.wodox.domain.base

abstract class BaseNoParamsUnsafeUseCase<out ResultType>: BaseUseCase() {
    
    abstract suspend fun execute(): ResultType

    suspend operator fun invoke(): ResultType {
        return withIOContext { execute() }
    }
} 