package com.wodox.main.ui.main

import com.wodox.main.ui.main.bottombar.BottomBarMenu.BottomBarMenuType
import com.wodox.main.ui.main.topbar.TopBarMenu

sealed class MainUiAction {
    data class ChangeTab(val type: BottomBarMenuType) : MainUiAction()
    data class ChangeTopTab(val type: TopBarMenu.TopBarMenuType) : MainUiAction()
}