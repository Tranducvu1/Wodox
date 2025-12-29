package com.wodox.docs.ui

import android.app.Application
import android.content.Context
import androidx.lifecycle.viewModelScope
import com.wodox.core.base.viewmodel.BaseUiStateViewModel
import com.wodox.core.extension.toArrayList
import com.wodox.docs.model.EventColorItem
import com.wodox.domain.docs.model.model.DocumentPermission
import com.wodox.domain.docs.model.model.InvitedUser
import com.wodox.domain.docs.model.model.SharedDocument
import com.wodox.domain.docs.model.usecase.GetSharedDocumentByIdUseCase
import com.wodox.domain.docs.model.usecase.GetSharedDocumentsForUserUseCase
import com.wodox.domain.docs.model.usecase.SaveSharedDocumentUseCase
import com.wodox.domain.docs.model.usecase.UpdateSharedDocumentUseCase
import com.wodox.domain.user.usecase.GetCurrentUser
import com.wodox.domain.user.usecase.GetUserById
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class DocViewModel @Inject constructor(
    private val app: Application,
    private val getUserById: GetUserById,
    private val getCurrentUser: GetCurrentUser,
    private val saveSharedDocument: SaveSharedDocumentUseCase,
    private val getSharedDocumentById: GetSharedDocumentByIdUseCase,
    private val getSharedDocumentsForUser: GetSharedDocumentsForUserUseCase,
    private val updateSharedDocument: UpdateSharedDocumentUseCase,
    ) : BaseUiStateViewModel<DocUiState, DocUiEvent, DocUiAction>(app) {

    override fun initialState(): DocUiState = DocUiState()

    var textFormat: com.wodox.domain.docs.model.TextFormat =
        com.wodox.domain.docs.model.TextFormat()

    init {
        loadColor()
        loadSharedDocumentsForCurrentUser()
    }

    override fun handleAction(action: DocUiAction) {
        super.handleAction(action)
        when (action) {
            is DocUiAction.UpdateTextSize -> updateTextSize(action.size)
            is DocUiAction.AssignUser -> loadUser(action.id)
            is DocUiAction.AddInvitedUser -> addInvitedUser(action.user)
            is DocUiAction.RemoveInvitedUser -> removeInvitedUser(action.userId)
            is DocUiAction.LoadInvitedUsers -> loadInvitedUsersState(action.invitedUsers)
            is DocUiAction.SetDocumentId -> updateState { it.copy(documentId = action.docId) }
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


    private fun loadUser(userId: UUID) {
        viewModelScope.launch(Dispatchers.IO) {
            val user = getUserById(userId)
            val currentUser = getCurrentUser()
            updateState {
                it.copy(
                    userInvite = user,
                    currentUser = currentUser
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
    }

    private fun removeInvitedUser(userId: UUID) {
        val currentUsers = uiState.value.invitedUsers
            .filter { it.userId != userId }

        updateState { it.copy(invitedUsers = currentUsers) }
        saveInvitedUsersToPreferences(currentUsers)

        val docId = uiState.value.documentId
        viewModelScope.launch(Dispatchers.IO) {
            val existingDoc = getSharedDocumentById(docId)
            if (existingDoc != null) {
                val updatedDoc = existingDoc.copy(invitedUsers = currentUsers)
                updateSharedDocument(updatedDoc)
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
            sharedPref.all.keys
                .filter { it.contains(docId) }
                .forEach { remove(it) }

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

            sharedPref.all.keys
                .filter { it.contains("_name_$docId") }
                .forEach { key ->
                    val userId = key
                        .replace("user_", "")
                        .replace("_name_$docId", "")
                    userIds.add(userId)
                }

            userIds.forEach { userId ->
                try {
                    val invitedUser = InvitedUser(
                        userId = UUID.fromString(userId),
                        userName = sharedPref.getString("user_${userId}_name_$docId", "") ?: "",
                        userEmail = sharedPref.getString("user_${userId}_email_$docId", "") ?: "",
                        permission = DocumentPermission.valueOf(
                            sharedPref.getString(
                                "user_${userId}_permission_$docId",
                                "VIEW"
                            ) ?: "VIEW"
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

            dispatch(DocUiAction.LoadInvitedUsers(invitedUsers))
        }
    }


    fun shareDocumentWithUsers(
        documentId: String,
        documentTitle: String,
        htmlContent: String
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val currentUser = getCurrentUser() ?: return@launch
                val invitedUsers = uiState.value.invitedUsers

                val sharedDoc = SharedDocument(
                    documentId = documentId,
                    documentTitle = documentTitle,
                    ownerUserId = currentUser.id,
                    ownerUserName = currentUser.name ?: "Unknown",
                    ownerUserEmail = currentUser.email ?: "",
                    invitedUsers = invitedUsers,
                    htmlContent = htmlContent,
                    lastModified = System.currentTimeMillis()
                )
                saveSharedDocument(sharedDoc)
                sendEvent(DocUiEvent.DocumentSharedSuccessfully(invitedUsers.size))
            } catch (e: Exception) {
                sendEvent(DocUiEvent.SharingFailed(e.message ?: "Unknown error"))
            }
        }
    }

    fun loadSharedDocumentsForCurrentUser() {
        viewModelScope.launch(Dispatchers.IO) {
            val currentUser = getCurrentUser() ?: return@launch
            val documents = getSharedDocumentsForUser(currentUser.id.toString())
            sendEvent(DocUiEvent.SharedDocumentsLoaded(documents))
        }
    }

    fun loadSharedDocumentById(docId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val document = getSharedDocumentById(docId)
            if (document != null) {
                sendEvent(DocUiEvent.DocumentLoadedFromServer(document))
            }
        }
    }

    fun updateSharedDocument(
        documentId: String,
        documentTitle: String,
        htmlContent: String
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val existingDoc = getSharedDocumentById(documentId) ?: return@launch
            val updatedDoc = existingDoc.copy(
                documentTitle = documentTitle,
                htmlContent = htmlContent,
                lastModified = System.currentTimeMillis()
            )
            updateSharedDocument(updatedDoc)
        }
    }
}
