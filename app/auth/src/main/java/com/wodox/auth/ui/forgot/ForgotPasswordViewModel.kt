package com.wodox.auth.ui.forgot

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.wodox.core.base.viewmodel.BaseUiStateViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ForgotPasswordViewModel @Inject constructor(
    app: Application
) : BaseUiStateViewModel<ForgotPasswordUiState, ForgotPasswordUiEvent, ForgotPasswordUiAction>(app) {

    override fun initialState(): ForgotPasswordUiState = ForgotPasswordUiState()

    private val auth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    override fun handleAction(action: ForgotPasswordUiAction) {
        when (action) {
            is ForgotPasswordUiAction.SendResetEmail -> sendPasswordResetEmail(action.email)
        }
    }

    private fun sendPasswordResetEmail(email: String) {
        viewModelScope.launch(Dispatchers.IO) {
            sendEvent(ForgotPasswordUiEvent.Loading(true))
            auth.sendPasswordResetEmail(email)
                .addOnCompleteListener { task ->
                    sendEvent(ForgotPasswordUiEvent.Loading(false))
                    if (task.isSuccessful) {
                        sendEvent(ForgotPasswordUiEvent.Success("Password reset email sent successfully. Please check your email."))
                    } else {
                        sendEvent(
                            ForgotPasswordUiEvent.Error(
                                task.exception?.message ?: "Failed to send reset email"
                            )
                        )
                    }
                }
        }
    }
}