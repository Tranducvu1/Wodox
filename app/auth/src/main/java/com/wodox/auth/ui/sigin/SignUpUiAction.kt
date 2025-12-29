package com.wodox.auth.ui.sigin

sealed class SignUpUiAction {
    data class SignUp(val email: String, val password: String,val fullName: String) : SignUpUiAction()
    data class SignIn(val email: String,val  password: String) : SignUpUiAction()
}