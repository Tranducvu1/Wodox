package com.wodox.auth.ui.sigin

sealed class SignUpUiEvent {
    data class Loading(val loading: Boolean) : SignUpUiEvent()
    data class Success(val message : String) : SignUpUiEvent()
    data class Error(val error : String) : SignUpUiEvent()
}