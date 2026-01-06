package com.wodox.auth.ui.forgot

sealed class ForgotPasswordUiAction {
    data class SendResetEmail(val email: String) : ForgotPasswordUiAction()
}
