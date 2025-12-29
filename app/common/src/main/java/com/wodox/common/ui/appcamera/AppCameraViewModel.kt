package com.wodox.common.ui.appcamera

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import android.app.Application
import com.wodox.core.base.viewmodel.TMVVMViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AppCameraViewModel @Inject constructor(
    override val app: Application,
) : TMVVMViewModel(app) {
    val isFlashTurnOn = MutableLiveData(false)
    var isCapturing = MutableLiveData(false)
    var resultUri: Uri? = null
}
