package com.wodox.docs.ui.docdetail

import com.wodox.domain.docs.model.model.SharedDocument

sealed class DocsDetailUiEvent {
    data class SharedDocumentsLoaded(val documents: List<SharedDocument>) : DocsDetailUiEvent()
    data class DocumentSharedSuccessfully(val userCount: Int) : DocsDetailUiEvent()
    data class SharingFailed(val message: String) : DocsDetailUiEvent()
    data class DocumentLoadedFromServer(val document: SharedDocument) : DocsDetailUiEvent()
}