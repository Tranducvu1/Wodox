package com.wodox.main.ui.main.editprofile

sealed class EditProfileUiAction {
    data class UpdateProfile(
        val fullName: String,
        val email: String,
        val phone: String,
        val bio: String,
    ) : EditProfileUiAction()
}