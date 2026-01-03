package com.wodox.docs.ui.docs

sealed class DocsUiAction {
    object LoadDocuments : DocsUiAction()
    data class DeleteDocument(val docId: String) : DocsUiAction()
    data class OpenDocument(val docId: String) : DocsUiAction()
    object CreateNewDocument : DocsUiAction()
}