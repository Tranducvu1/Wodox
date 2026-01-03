package com.wodox.docs.ui.docdetail

import android.app.Application
import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.wodox.core.base.viewmodel.BaseUiStateViewModel
import com.wodox.core.extension.toArrayList
import com.wodox.docs.model.EventColorItem
import com.wodox.domain.docs.model.TextFormat
import com.wodox.domain.docs.model.model.DocumentPermission
import com.wodox.domain.docs.model.model.InvitedUser
import com.wodox.domain.docs.model.model.SharedDocument
import com.wodox.domain.docs.model.usecase.GetSharedDocumentByIdUseCase
import com.wodox.domain.docs.model.usecase.GetSharedDocumentsForUserUseCase
import com.wodox.domain.docs.model.usecase.SaveSharedDocumentUseCase
import com.wodox.domain.docs.model.usecase.UpdateSharedDocumentUseCase
import com.wodox.domain.user.usecase.GetCurrentUser
import com.wodox.domain.user.usecase.GetUserById
import com.wodox.docs.model.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class DocsDetailViewModel @Inject constructor(
    private val app: Application,
    private val getUserById: GetUserById,
    private val getCurrentUser: GetCurrentUser,
    private val saveSharedDocument: SaveSharedDocumentUseCase,
    private val getSharedDocumentByIdUseCase: GetSharedDocumentByIdUseCase,
    private val getSharedDocumentsForUser: GetSharedDocumentsForUserUseCase,
    private val updateSharedDocumentUseCase: UpdateSharedDocumentUseCase,
    private val savedStateHandle: SavedStateHandle
) : BaseUiStateViewModel<DocsDetailState, DocsDetailUiEvent, DocsDetailUiAction>(app) {

    override fun initialState(): DocsDetailState = DocsDetailState()

    val documentId: String? = savedStateHandle[Constants.Intents.SHARE_DOCUMENT]

    var textFormat: TextFormat = TextFormat()

    init {
        loadColor()
        loadUser()
        loadSharedDocumentsForCurrentUser()
        documentId?.let {
            loadSharedDocumentById(it)
        }
    }

    override fun handleAction(action: DocsDetailUiAction) {
        super.handleAction(action)
        when (action) {
            is DocsDetailUiAction.UpdateTextSize -> updateTextSize(action.size)
            is DocsDetailUiAction.AssignUser -> assignUser(action.id)
            is DocsDetailUiAction.AddInvitedUser -> addInvitedUser(action.user)
            is DocsDetailUiAction.RemoveInvitedUser -> removeInvitedUser(action.userId)
            is DocsDetailUiAction.LoadInvitedUsers -> loadInvitedUsersState(action.invitedUsers)
            is DocsDetailUiAction.SetDocumentId -> updateState { it.copy(documentId = action.docId) }
            else -> Unit
        }
    }

    private fun loadColor() {
        val colors = EventColorItem.getDefaults().toArrayList()
        updateState { it.copy(colors = colors) }
    }

    private fun updateTextSize(size: Int) {
        updateState { it.copy(textSize = size) }
    }

    private fun loadUser() {
        viewModelScope.launch(Dispatchers.IO) {
            val currentUser = getCurrentUser()
            updateState {
                it.copy(
                    currentUser = currentUser
                )
            }
        }
    }

    private fun assignUser(userId: UUID) {
        viewModelScope.launch(Dispatchers.IO) {
            val user = getUserById(userId)
            updateState {
                it.copy(
                    userInvite = user,
                )
            }
        }
    }

    private fun addInvitedUser(user: InvitedUser) {
        val currentUsers = uiState.value.invitedUsers.toMutableList()
        if (currentUsers.any { it.userId == user.userId }) return

        currentUsers.add(user)
        updateState { it.copy(invitedUsers = currentUsers) }
        saveInvitedUsersToPreferences(currentUsers)

        val docId = uiState.value.documentId
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val existingDoc = getSharedDocumentByIdUseCase(docId)
                if (existingDoc != null) {
                    val updatedDoc = existingDoc.copy(
                        invitedUsers = currentUsers,
                        lastModified = System.currentTimeMillis()
                    )
                    updateSharedDocumentUseCase(updatedDoc)
                    android.util.Log.d("DocsDetailViewModel", "Updated invitedUsers in Firestore")
                } else {
                    android.util.Log.w(
                        "DocsDetailViewModel",
                        "Document not found, will be created on save"
                    )
                }
            } catch (e: Exception) {
                android.util.Log.e("DocsDetailViewModel", "Error updating invitedUsers", e)
            }
        }
    }

    private fun removeInvitedUser(userId: UUID) {
        val currentUsers = uiState.value.invitedUsers.filter { it.userId != userId }

        updateState { it.copy(invitedUsers = currentUsers) }
        saveInvitedUsersToPreferences(currentUsers)

        val docId = uiState.value.documentId
        viewModelScope.launch(Dispatchers.IO) {
            val existingDoc = getSharedDocumentByIdUseCase(docId)
            if (existingDoc != null) {
                val updatedDoc = existingDoc.copy(invitedUsers = currentUsers)
                updateSharedDocumentUseCase(updatedDoc)
            }
        }
    }

    private fun loadInvitedUsersState(users: List<InvitedUser>) {
        updateState { it.copy(invitedUsers = users) }
    }

    private fun saveInvitedUsersToPreferences(users: List<InvitedUser>) {
        val sharedPref = app.getSharedPreferences("doc_invites", Context.MODE_PRIVATE)
        val docId = uiState.value.documentId

        sharedPref.edit().apply {
            sharedPref.all.keys.filter { it.contains(docId) }.forEach { remove(it) }

            users.forEach { user ->
                putString("user_${user.userId}_name_$docId", user.userName)
                putString("user_${user.userId}_email_$docId", user.userEmail)
                putString("user_${user.userId}_permission_$docId", user.permission.name)
                putLong("user_${user.userId}_invited_at_$docId", user.invitedAt)
            }
            apply()
        }
    }

    fun loadInvitedUsersFromPreferences(docId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val sharedPref = app.getSharedPreferences("doc_invites", Context.MODE_PRIVATE)

            val invitedUsers = mutableListOf<InvitedUser>()
            val userIds = mutableSetOf<String>()

            sharedPref.all.keys.filter { it.contains("_name_$docId") }.forEach { key ->
                val userId = key.replace("user_", "").replace("_name_$docId", "")
                userIds.add(userId)
            }

            userIds.forEach { userId ->
                try {
                    val invitedUser = InvitedUser(
                        userId = UUID.fromString(userId),
                        userName = sharedPref.getString("user_${userId}_name_$docId", "") ?: "",
                        userEmail = sharedPref.getString("user_${userId}_email_$docId", "") ?: "",
                        permission = DocumentPermission.valueOf(
                            sharedPref.getString("user_${userId}_permission_$docId", "VIEW")
                                ?: "VIEW"
                        ),
                        invitedAt = sharedPref.getLong(
                            "user_${userId}_invited_at_$docId",
                            System.currentTimeMillis()
                        )
                    )
                    invitedUsers.add(invitedUser)
                } catch (_: Exception) {
                }
            }

            dispatch(DocsDetailUiAction.LoadInvitedUsers(invitedUsers))
        }
    }

    fun shareDocumentWithUsers(
        documentId: String,
        documentTitle: String,
        htmlContent: String
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                android.util.Log.d("DocsDetailViewModel", "=== START shareDocumentWithUsers ===")

                val currentUser = getCurrentUser()
                if (currentUser == null) {
                    android.util.Log.e("DocsDetailViewModel", "Current user is null")
                    sendEvent(DocsDetailUiEvent.SharingFailed("User not logged in"))
                    return@launch
                }

                android.util.Log.d(
                    "DocsDetailViewModel",
                    "Current user: ${currentUser.id} - ${currentUser.name}"
                )

                val invitedUsers = uiState.value.invitedUsers
                android.util.Log.d(
                    "DocsDetailViewModel",
                    "Invited users count: ${invitedUsers.size}"
                )

                val sharedDoc = SharedDocument(
                    documentId = documentId,
                    documentTitle = documentTitle,
                    ownerUserId = currentUser.id.toString(),
                    ownerUserName = currentUser.name,
                    ownerUserEmail = currentUser.email,
                    invitedUsers = invitedUsers,
                    htmlContent = htmlContent,
                    sharedAt = System.currentTimeMillis(),
                    lastModified = System.currentTimeMillis()
                )

                android.util.Log.d("DocsDetailViewModel", "Saving document:")
                android.util.Log.d("DocsDetailViewModel", "  - documentId: ${sharedDoc.documentId}")
                android.util.Log.d(
                    "DocsDetailViewModel",
                    "  - ownerUserId: ${sharedDoc.ownerUserId}"
                )
                android.util.Log.d(
                    "DocsDetailViewModel",
                    "  - ownerUserName: ${sharedDoc.ownerUserName}"
                )

                saveSharedDocument(sharedDoc)

                android.util.Log.d("DocsDetailViewModel", "Document saved successfully")
                sendEvent(DocsDetailUiEvent.DocumentSharedSuccessfully(invitedUsers.size))

            } catch (e: Exception) {
                android.util.Log.e("DocsDetailViewModel", "Error sharing document", e)
                sendEvent(DocsDetailUiEvent.SharingFailed(e.message ?: "Unknown error"))
            }
        }
    }

    fun loadSharedDocumentsForCurrentUser() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val currentUser = getCurrentUser() ?: return@launch

                val documents = getSharedDocumentsForUser(currentUser.id.toString())

                sendEvent(DocsDetailUiEvent.SharedDocumentsLoaded(documents))
            } catch (e: Exception) {
                android.util.Log.e("DocsDetailViewModel", "Error loading documents", e)
            }
        }
    }

    fun loadSharedDocumentById(docId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val document = getSharedDocumentByIdUseCase(docId)
            if (document != null) {
                sendEvent(DocsDetailUiEvent.DocumentLoadedFromServer(document))
            }
        }
    }

    fun updateSharedDocument(
        documentId: String,
        documentTitle: String,
        htmlContent: String
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val existingDoc = getSharedDocumentByIdUseCase(documentId)

                if (existingDoc != null) {
                    val currentInvitedUsers = uiState.value.invitedUsers

                    val updatedDoc = existingDoc.copy(
                        documentTitle = documentTitle,
                        htmlContent = htmlContent,
                        invitedUsers = currentInvitedUsers,
                        lastModified = System.currentTimeMillis()
                    )

                    android.util.Log.d("DocsDetailViewModel", "Updating document: $documentId")
                    android.util.Log.d(
                        "DocsDetailViewModel",
                        "Invited users: ${currentInvitedUsers.size}"
                    )

                    updateSharedDocumentUseCase(updatedDoc)

                    android.util.Log.d("DocsDetailViewModel", "Document updated successfully")
                } else {
                    android.util.Log.w("DocsDetailViewModel", "Document not found: $documentId")
                }

            } catch (e: Exception) {
                android.util.Log.e("DocsDetailViewModel", "Error updating document", e)
            }
        }
    }
}