package com.wodox.docs.ui.docs

import com.wodox.domain.docs.model.model.SharedDocument
import com.wodox.domain.user.model.User

data class DocsUiState(
    val documents: List<SharedDocument> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val currentUser: User? = null
)