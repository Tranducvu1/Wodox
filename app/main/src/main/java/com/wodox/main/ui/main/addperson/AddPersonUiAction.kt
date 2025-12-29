package com.wodox.main.ui.main.addperson

import java.util.UUID

sealed class AddPersonUiAction {
    data class FindUserEmail(val email:String) : AddPersonUiAction()

    data class HandleMakeFriend(val userId: UUID) : AddPersonUiAction()
}