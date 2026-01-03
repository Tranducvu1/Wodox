package com.wodox.docs.ui.docs

sealed class DocsUiEvent {
    data class ShowError(val message: String) : DocsUiEvent()
    data class DocumentDeleted(val docId: String) : DocsUiEvent()
    object NavigateToCreateDoc : DocsUiEvent()
}