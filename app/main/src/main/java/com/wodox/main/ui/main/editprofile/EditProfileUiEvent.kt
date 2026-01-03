package com.wodox.main.ui.main.editprofile

sealed class EditProfileUiEvent {
    data class ShowError(val message: String) : EditProfileUiEvent()
    object UpdateSuccess : EditProfileUiEvent()
    data class Loading(val isLoading: Boolean) : EditProfileUiEvent()
}