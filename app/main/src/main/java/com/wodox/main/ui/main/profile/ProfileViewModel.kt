package com.wodox.main.ui.main.profile

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.wodox.core.base.viewmodel.BaseUiStateViewModel
import com.wodox.domain.home.repository.SettingsRepository
import com.wodox.domain.main.model.Item
import com.wodox.domain.main.usecase.GetAllItemProfileSetting
import com.wodox.domain.user.usecase.SignOutUseCase
import com.wodox.ui.notification.manager.TaskDeadlineNotificationManager
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
    private val settingsRepository: SettingsRepository
) : BaseUiStateViewModel<ProfileUiState, ProfileUiEvent, ProfileUiAction>(app) {

    override fun initialState(): ProfileUiState = ProfileUiState()

    val item: StateFlow<List<Item>> = getAllItemProfileSetting()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = emptyList()
        )

    val isNotificationEnabled: StateFlow<Boolean> = settingsRepository.isNotificationEnabled
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            false
        )

    override fun handleAction(action: ProfileUiAction) {
        super.handleAction(action)
        when (action) {
            ProfileUiAction.SignOut -> handleSignOut()
            is ProfileUiAction.ToggleNotification -> setNotificationEnabled(action.enabled)
        }
    }

    private fun setNotificationEnabled(enabled: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            settingsRepository.setNotificationEnabled(enabled)
            if (enabled) {
                TaskDeadlineNotificationManager.startDeadlineCheck(app)
            } else {
                TaskDeadlineNotificationManager.stopDeadlineCheck(app)
            }
        }
    }

    private fun handleSignOut() {
        viewModelScope.launch(Dispatchers.IO) {
            singoutUseCase()
            sendEvent(ProfileUiEvent.NavigateSignOut)
        }
    }
}