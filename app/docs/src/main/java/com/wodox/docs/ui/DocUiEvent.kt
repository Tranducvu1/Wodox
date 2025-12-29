package com.wodox.docs.ui

import com.wodox.domain.docs.model.model.SharedDocument

sealed class DocUiEvent {
    data class SharedDocumentsLoaded(val documents: List<SharedDocument>) : DocUiEvent()
    data class DocumentSharedSuccessfully(val userCount: Int) : DocUiEvent()
    data class SharingFailed(val message: String) : DocUiEvent()
    data class DocumentLoadedFromServer(val document: SharedDocument) : DocUiEvent()
}