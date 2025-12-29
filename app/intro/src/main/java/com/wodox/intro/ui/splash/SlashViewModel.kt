package com.wodox.intro.ui.splash

import android.app.Application
import com.wodox.core.base.viewmodel.BaseUiStateViewModel
import com.wodox.intro.ui.intro.IntroUiAction
import com.wodox.intro.ui.intro.IntroUiEvent
import com.wodox.intro.ui.intro.IntroUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SlashViewModel @Inject constructor(
     val app: Application) :
    BaseUiStateViewModel<IntroUiState, IntroUiEvent, IntroUiAction>(app) {
    override fun initialState(): IntroUiState = IntroUiState()
}