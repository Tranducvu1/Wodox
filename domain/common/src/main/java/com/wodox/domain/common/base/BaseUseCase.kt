package com.wodox.domain.common.base

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

abstract class BaseUseCase {
    protected suspend fun <T> withIOContext(block: suspend CoroutineScope.() -> T): T {
        return withContext(Dispatchers.IO, block)
    }

    protected suspend fun <T> withMainContext(block: suspend CoroutineScope.() -> T): T {
        return withContext(Dispatchers.Main, block)
    }

    protected suspend fun <T> withDefaultContext(block: suspend CoroutineScope.() -> T): T {
        return withContext(Dispatchers.Default, block)
    }

    protected fun handleError(throwable: Throwable): Throwable {
        return when (throwable) {
            is UnknownHostException -> NetworkError.NoInternet
            is SocketTimeoutException -> NetworkError.Timeout
            is IOException -> NetworkError.ServerError
            else -> NetworkError.Unknown(throwable)
        }
    }

    protected fun <T> Result<T>.onSuccess(action: (T) -> Unit): Result<T> {
        if (this is Result.Success) {
            action(data)
        }
        return this
    }

    protected fun <T> Result<T>.onError(action: (Throwable) -> Unit): Result<T> {
        if (this is Result.Error) {
            action(exception)
        }
        return this
    }

    protected fun <T> Result<T>.onLoading(action: () -> Unit): Result<T> {
        if (this is Result.Loading) {
            action()
        }
        return this
    }
}