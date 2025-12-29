package com.wodox.main.ui.main.addperson

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.wodox.core.base.viewmodel.BaseUiStateViewModel
import com.wodox.domain.home.model.local.FriendStatus
import com.wodox.domain.home.model.local.UserFriend
import com.wodox.domain.home.usecase.AddFriendUseCase
import com.wodox.domain.user.usecase.GetCurrentUserEmail
import com.wodox.domain.user.usecase.GetUserByEmailUseCase
import com.wodox.domain.user.usecase.GetUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Date
import java.util.UUID
import javax.inject.Inject


@HiltViewModel
class AddPersonUiViewModel @Inject constructor(
    val app: Application,
    private val getUserByEmailUseCase: GetUserByEmailUseCase,
    private val getCurrentUserEmail: GetCurrentUserEmail,
    private val addFriendUseCase: AddFriendUseCase,
    private val getUserUseCase: GetUserUseCase,
) : BaseUiStateViewModel<AddPersonUiState, AddPersonUiEvent, AddPersonUiAction>(app) {

    override fun initialState(): AddPersonUiState = AddPersonUiState()

    override fun handleAction(action: AddPersonUiAction) {
        super.handleAction(action)
        when (action) {
            is AddPersonUiAction.FindUserEmail -> handleGetUserByEmailUseCase(action.email)
            is AddPersonUiAction.HandleMakeFriend -> handleAddFriend(action.userId)
        }
    }

    init {
        loadUser()
    }

    private fun handleGetUserByEmailUseCase(email: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                updateState {
                    it.copy(isLoading = true)
                }

                val currentEmail = uiState.value.email
                val currentUserId = uiState.value.userId

                if (email.isBlank()) {
                    updateState {
                        it.copy(users = emptyList(), isLoading = false)
                    }
                    return@launch
                }
                if (email.equals(currentEmail, ignoreCase = true)) {
                    updateState {
                        it.copy(
                            users = emptyList(),
                            isLoading = false,
                            errorMessage = "Cannot add yourself as friend"
                        )
                    }
                    return@launch
                }

                val user = getUserByEmailUseCase(email)

                val updatedUsers = if (user != null && user.id != currentUserId) {
                    listOf(user)
                } else {
                    emptyList()
                }

                updateState {
                    it.copy(
                        users = updatedUsers,
                        isLoading = false,
                        errorMessage = if (updatedUsers.isEmpty()) "User not found" else null
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
                updateState {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Error finding user: ${e.message}"
                    )
                }
            }
        }
    }

    private fun loadUser() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val email = getCurrentUserEmail()
                val userID = getUserUseCase()
                updateState {
                    it.copy(
                        email = email,
                        userId = userID
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun handleAddFriend(connectId: UUID) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val currentUserId = uiState.value.userId ?: return@launch

                // Không cho phép add chính mình
                if (connectId == currentUserId) {
                    updateState {
                        it.copy(errorMessage = "Cannot add yourself as friend")
                    }
                    return@launch
                }

                val addPerson = UserFriend(
                    id = UUID.randomUUID(),
                    userId = currentUserId,
                    friendId = connectId,
                    status = FriendStatus.PENDING,
                    createdAt = Date()
                )
                addFriendUseCase(addPerson)

                sendEvent(AddPersonUiEvent.AddFriendSuccess)

                updateState {
                    it.copy(users = emptyList())
                }
            } catch (e: Exception) {
                e.printStackTrace()
                updateState {
                    it.copy(errorMessage = "Error adding friend: ${e.message}")
                }
            }
        }
    }
}