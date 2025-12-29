package com.wodox.auth.ui.sigin

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.wodox.auth.model.Constants
import com.wodox.core.base.viewmodel.BaseUiStateViewModel
import com.wodox.domain.user.model.User
import com.wodox.domain.user.usecase.SaveUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Date
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    val app: Application,
    private val saveUserUseCase: SaveUserUseCase,
) : BaseUiStateViewModel<SignUpUiState, SignUpUiEvent, SignUpUiAction>(app) {

    override fun initialState(): SignUpUiState = SignUpUiState()

    private val auth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    val isShowSignUp: Boolean by lazy {
        data?.getBoolean(Constants.Intents.IS_SHOW_SIGN_UP) ?: false
    }

    override fun handleAction(action: SignUpUiAction) {
        when (action) {
            is SignUpUiAction.SignUp -> signUp(action.email, action.password, action.fullName)
            is SignUpUiAction.SignIn -> signIn(action.email, action.password)
        }
    }

    private fun signUp(email: String, password: String, fullName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            withContext(Dispatchers.Main) {
                sendEvent(SignUpUiEvent.Loading(true))
            }
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    sendEvent(SignUpUiEvent.Loading(false))
                    if (task.isSuccessful) {
                        val firebaseUser = auth.currentUser
                        val profileUpdates = UserProfileChangeRequest.Builder()
                            .setDisplayName(fullName)
                            .build()
                        firebaseUser?.updateProfile(profileUpdates)
                        val user = User(
                            id = UUID.nameUUIDFromBytes(firebaseUser?.uid?.toByteArray()),
                            email = email,
                            name = fullName,
                            password = password,
                            isActive = true,
                            updatedAt = Date()
                        )
                        viewModelScope.launch(Dispatchers.IO) {
                            val result = saveUserUseCase(user)
                            if (result != null) {
                                sendEvent(SignUpUiEvent.Success("Account created successfully"))
                            } else {
                                sendEvent(SignUpUiEvent.Error("Failed to save user info"))
                            }
                        }
                        sendEvent(SignUpUiEvent.Success("Account created successfully"))
                    } else {

                        sendEvent(SignUpUiEvent.Error(task.exception?.message ?: "Sign-up failed"))
                    }
                }
        }
    }

    fun signIn(email: String, password: String) {
        viewModelScope.launch(Dispatchers.IO) {
            sendEvent(SignUpUiEvent.Loading(true))
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    sendEvent(SignUpUiEvent.Loading(false))
                    if (task.isSuccessful) {
                        sendEvent(SignUpUiEvent.Success("Sign-in successful"))
                    } else {
                        sendEvent(SignUpUiEvent.Error(task.exception?.message ?: "Sign-in failed"))
                    }
                }
        }
    }
}
