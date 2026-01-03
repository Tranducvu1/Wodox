package com.wodox.docs.ui.docs

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.wodox.core.base.viewmodel.BaseUiStateViewModel
import com.wodox.domain.docs.model.repository.SharedDocumentRepository
import com.wodox.domain.docs.model.usecase.GetDocumentsByUserIdUseCase
import com.wodox.domain.user.usecase.GetCurrentUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DocsViewModel @Inject constructor(
    private val app: Application,
    private val sharedDocumentRepository: SharedDocumentRepository,
    private val getDocumentsByUserIdUseCase: GetDocumentsByUserIdUseCase,
    private val getCurrentUser: GetCurrentUser

) : BaseUiStateViewModel<DocsUiState, DocsUiEvent, DocsUiAction>(app) {

    override fun initialState(): DocsUiState = DocsUiState()

    init {
        loadCurrentUser()
        loadDocuments()
    }

    override fun handleAction(action: DocsUiAction) {
        super.handleAction(action)
        when (action) {
            is DocsUiAction.LoadDocuments -> loadDocuments()
            is DocsUiAction.DeleteDocument -> deleteDocument(action.docId)
            is DocsUiAction.OpenDocument -> {
                // Handle in Fragment
            }

            is DocsUiAction.CreateNewDocument -> {
                sendEvent(DocsUiEvent.NavigateToCreateDoc)
            }
        }
    }

    private fun loadDocuments() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                updateState { it.copy(isLoading = true, error = null) }

                val currentUser = getCurrentUser()

                if (currentUser == null) {
                    updateState {
                        it.copy(
                            isLoading = false,
                            error = "Please login to view documents"
                        )
                    }
                    return@launch
                }

                val userId = currentUser.id.toString()

                val ownedDocuments = getDocumentsByUserIdUseCase(userId)

                val sharedDocuments = sharedDocumentRepository.getSharedDocumentsForUser(userId)

                val allDocuments = (ownedDocuments + sharedDocuments)
                    .distinctBy { it.documentId }
                    .sortedByDescending { it.lastModified }

                android.util.Log.d("DocsViewModel", "Owned documents: ${ownedDocuments.size}")
                android.util.Log.d("DocsViewModel", "Shared documents: ${sharedDocuments.size}")
                android.util.Log.d("DocsViewModel", "Total documents: ${allDocuments.size}")

                updateState {
                    it.copy(
                        documents = allDocuments,
                        isLoading = false,
                        error = null
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
                updateState {
                    it.copy(
                        isLoading = false,
                        error = "Failed to load documents: ${e.message}"
                    )
                }
                sendEvent(DocsUiEvent.ShowError(e.message ?: "Unknown error"))
            }
        }
    }

    private fun loadCurrentUser() {
        viewModelScope.launch(Dispatchers.IO) {
            val user = getCurrentUser()
            android.util.Log.d("DocsViewModel", "=== loadCurrentUser ===")
            android.util.Log.d("DocsViewModel", "Current user: ${user?.id} - ${user?.name}")
            updateState { it.copy(currentUser = user) }
        }
    }

    private fun deleteDocument(docId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                updateState { it.copy(isLoading = true) }

                sharedDocumentRepository.deleteSharedDocument(docId)

                sendEvent(DocsUiEvent.DocumentDeleted(docId))

                updateState {
                    it.copy(
                        documents = it.documents.filter { doc -> doc.documentId != docId },
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
                updateState { it.copy(isLoading = false) }
                sendEvent(DocsUiEvent.ShowError("Failed to delete: ${e.message}"))
            }
        }
    }
}