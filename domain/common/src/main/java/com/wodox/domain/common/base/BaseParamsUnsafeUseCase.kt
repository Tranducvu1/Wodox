package com.wodox.domain.common.base

abstract class BaseParamsUnsafeUseCase<in Params, out ResultType> : BaseUseCase() {
    abstract suspend fun execute(params: Params): ResultType

    suspend operator fun invoke(params: Params): ResultType {
        return withIOContext { execute(params) }
    }
}
