package com.wodox.main.ui.main.profile

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.wodox.core.base.viewmodel.BaseUiStateViewModel
import com.wodox.domain.main.model.Item
import com.wodox.domain.main.usecase.GetAllItemProfileSetting
import com.wodox.domain.user.usecase.SignOutUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ProfileViewModel @Inject constructor(
    val app: Application,
    private val getAllItemProfileSetting: GetAllItemProfileSetting,
    private val singoutUseCase: SignOutUseCase,
) : BaseUiStateViewModel<ProfileUiState, ProfileUiEvent, ProfileUiAction>(app) {
    override fun initialState(): ProfileUiState = ProfileUiState()

    val item: StateFlow<List<Item>> = getAllItemProfileSetting()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = emptyList()
        )


    override fun handleAction(action: ProfileUiAction) {
        super.handleAction(action)
        when(action){
            ProfileUiAction.SignOut -> handleSignOut()
        }

    }

    private fun handleSignOut(){
        viewModelScope.launch(Dispatchers.IO) {
            singoutUseCase()
            sendEvent(ProfileUiEvent.NavigateSignOut)
        }
    }
}