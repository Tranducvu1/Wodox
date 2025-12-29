package com.wodox.intro.ui.intro

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.wodox.data.common.datasource.AppSharePrefs
import com.wodox.core.base.viewmodel.BaseUiStateViewModel
import com.wodox.intro.model.IntroData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class IntroViewModel @Inject constructor(
     val app: Application,
    val appSharePrefs: AppSharePrefs
) : BaseUiStateViewModel<IntroUiState, IntroUiEvent, IntroUiAction>(app) {

    val intros = MutableLiveData<ArrayList<IntroData>>(IntroData.getDefault(applicationContext()))


    override fun initialState(): IntroUiState = IntroUiState()

    override fun onCreate() {
        super.onCreate()
    }

    override fun handleAction(action: IntroUiAction) {
        when (action) {
            is IntroUiAction.OnFinishIntro -> onFinishIntro()
            is IntroUiAction.OnPageChanged -> onPageChanged(action.position)
        }
    }

    fun onFinishIntro() {
        viewModelScope.launch(Dispatchers.IO) {
            appSharePrefs.isFirstOpen = false
            withContext(Dispatchers.Main) {
                sendEvent(IntroUiEvent.NavigateToMain)
            }
        }
    }

    fun onPageChanged(position: Int) {
        updateState {
            it.copy(currentPage = position)
        }
    }

}