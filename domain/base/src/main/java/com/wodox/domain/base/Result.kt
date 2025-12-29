package com.wodox.domain.base

sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val exception: Throwable) : Result<Nothing>()
    data object Loading : Result<Nothing>()
}

val <T> Result<T>.data: T?
    get() = (this as? Result.Success<T>)?.data