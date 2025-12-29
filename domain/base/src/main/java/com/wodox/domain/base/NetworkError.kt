package com.wodox.domain.base

sealed class NetworkError : Throwable() {
    data object NoInternet : NetworkError() {
        private fun readResolve(): Any = NoInternet
    }

    data object Timeout : NetworkError() {
        private fun readResolve(): Any = Timeout
    }

    data object ServerError : NetworkError() {
        private fun readResolve(): Any = ServerError
    }

    data class Unknown(override val cause: Throwable) : NetworkError()
}