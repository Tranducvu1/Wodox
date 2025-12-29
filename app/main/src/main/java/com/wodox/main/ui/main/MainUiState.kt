package com.wodox.main.ui.main

import com.wodox.domain.user.model.User
import com.wodox.main.ui.main.bottombar.BottomBarMenu
import com.wodox.main.ui.main.topbar.TopBarMenu
import java.util.UUID

data class MainUiState(
    val menus: List<BottomBarMenu> = arrayListOf(),
    val menusTopbar: List<TopBarMenu> = arrayListOf(),
    val email: String? = null,
    val userId: UUID? = null
)