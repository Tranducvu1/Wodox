package com.wodox.main.ui.main.editprofile

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.wodox.core.base.viewmodel.BaseUiStateViewModel
import com.wodox.domain.user.usecase.GetCurrentUser
import com.wodox.domain.user.usecase.UpdateUserProfileParams
import com.wodox.domain.user.usecase.UpdateUserProfileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    val app: Application,
    private val getCurrentUserUseCase: GetCurrentUser,
    private val updateUserProfileUseCase: UpdateUserProfileUseCase
) : BaseUiStateViewModel<EditProfileUiState, EditProfileUiEvent, EditProfileUiAction>(app) {
    override fun initialState(): EditProfileUiState = EditProfileUiState()

    init {
        loadUserProfile()
    }

    override fun handleAction(action: EditProfileUiAction) {
        super.handleAction(action)
        when (action) {
            is EditProfileUiAction.UpdateProfile -> updateUserProfile(action)
        }
    }

    private fun loadUserProfile() {
        viewModelScope.launch(Dispatchers.IO) {
            sendEvent(EditProfileUiEvent.Loading(true))

            try {
                val user = getCurrentUserUseCase()

                if (user != null) {
                    updateState {
                        it.copy(
                            fullName = user.name,
                            email = user.email,
                            phone = user.phone ?: "",
                            bio = user.bio ?: ""
                        )
                    }
                } else {
                    sendEvent(EditProfileUiEvent.ShowError("Unable to load user profile"))
                }
            } catch (e: Exception) {
                sendEvent(EditProfileUiEvent.ShowError(e.message ?: "Error loading profile"))
            }

            sendEvent(EditProfileUiEvent.Loading(false))
        }
    }

    private fun updateUserProfile(action: EditProfileUiAction.UpdateProfile) {
        viewModelScope.launch(Dispatchers.IO) {
            sendEvent(EditProfileUiEvent.Loading(true))

            try {
                val params = UpdateUserProfileParams(
                    fullName = action.fullName,
                    email = action.email,
                    phone = action.phone,
                    bio = action.bio
                )

                val result = updateUserProfileUseCase(params)

                if (result != null) {
                    updateState {
                        it.copy(
                            fullName = action.fullName,
                            email = action.email,
                            phone = action.phone,
                            bio = action.bio
                        )
                    }
                    sendEvent(EditProfileUiEvent.UpdateSuccess)
                } else {
                    sendEvent(EditProfileUiEvent.ShowError("Failed to update profile"))
                }
            } catch (e: Exception) {
                sendEvent(EditProfileUiEvent.ShowError(e.message ?: "An error occurred"))
            }

            sendEvent(EditProfileUiEvent.Loading(false))
        }
    }

    fun clearError() {

    }
}
