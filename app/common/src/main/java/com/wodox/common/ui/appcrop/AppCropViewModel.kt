package com.wodox.common.ui.appcrop

import android.app.Application
import com.wodox.core.base.viewmodel.TMVVMViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AppCropViewModel@Inject constructor(
     override val app: Application,
) : TMVVMViewModel(app) {
}