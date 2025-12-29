package com.wodox.main.ui.main

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.wodox.core.base.viewmodel.BaseUiStateViewModel
import com.wodox.core.extension.toArrayList
import com.wodox.domain.user.usecase.GetCurrentUserEmail
import com.wodox.domain.user.usecase.GetUserUseCase
import com.wodox.main.ui.main.bottombar.BottomBarMenu
import com.wodox.main.ui.main.topbar.TopBarMenu
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    val app: Application,
    private val getCurrentUserEmail: GetCurrentUserEmail,
    private val getUserUseCase: GetUserUseCase,
) : BaseUiStateViewModel<MainUiState, MainUiEvent, MainUiAction>(app) {

    val bottomBarMenus: List<BottomBarMenu> = BottomBarMenu.getDefaults(applicationContext())
    val topBarMenus: List<TopBarMenu> = TopBarMenu.getDefaults(applicationContext())

    val changePageEvent = MutableLiveData<Int>()

    val changePageBottomEvent = MutableLiveData<Int>()

    override fun initialState(): MainUiState = MainUiState()

    override fun handleAction(action: MainUiAction) {
        when (action) {
            is MainUiAction.ChangeTab -> changeTab(action.type)
            is MainUiAction.ChangeTopTab -> changeTopTab(action.type)
        }
    }

    override fun onCreate() {
        super.onCreate()
        loadUser()
    }

    private fun changeTab(type: BottomBarMenu.BottomBarMenuType) {
        val menus = uiState.value.menus.toArrayList().onEach {
            it.isSelected = it.type == type
        }
        updateState { it.copy(menus = menus) }
        changePageBottomEvent.value = when (type) {
            BottomBarMenu.BottomBarMenuType.HOME -> 0
            BottomBarMenu.BottomBarMenuType.ACTIVITY -> 1
            BottomBarMenu.BottomBarMenuType.CREATE -> 2
            BottomBarMenu.BottomBarMenuType.MY_WORK -> 3
        }
    }

    private fun changeTopTab(type: TopBarMenu.TopBarMenuType) {
        val menus = uiState.value.menusTopbar.toArrayList().onEach {
            it.isSelected = it.type == type
        }

        updateState { it.copy(menusTopbar = menus) }

        changePageEvent.value = when (type) {
            TopBarMenu.TopBarMenuType.RECENT -> 0
            TopBarMenu.TopBarMenuType.FAVOURITE -> 1
            TopBarMenu.TopBarMenuType.CALENDER -> 2
            TopBarMenu.TopBarMenuType.DOCS -> 3
            TopBarMenu.TopBarMenuType.MY_WORK -> 4
        }
    }

    private fun loadUser() {
        viewModelScope.launch(Dispatchers.IO) {
            val email = getCurrentUserEmail()
            val userID = getUserUseCase()
            updateState {
                it.copy(
                    email = email,
                    userId = userID
                )
            }
        }
    }
}
