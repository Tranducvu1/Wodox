package com.wodox.auth.ui.forgot

sealed class ForgotPasswordUiEvent {
    data class Loading(val loading: Boolean) : ForgotPasswordUiEvent()
    data class Success(val message: String) : ForgotPasswordUiEvent()
    data class Error(val error: String) : ForgotPasswordUiEvent()
}