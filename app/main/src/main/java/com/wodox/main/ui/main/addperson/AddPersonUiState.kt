package com.wodox.main.ui.main.addperson

import com.wodox.domain.user.model.User
import java.util.UUID

data class AddPersonUiState(
    val users: List<User> = emptyList(),
    val isLoading: Boolean = false,
    val email: String? = null,
    val userId: UUID? = null,
    val errorMessage:String?= null
)
