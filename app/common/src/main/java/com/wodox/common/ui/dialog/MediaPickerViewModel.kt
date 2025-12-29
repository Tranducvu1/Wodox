package com.wodox.common.ui.dialog

import android.app.Application
import com.wodox.core.base.viewmodel.TMVVMViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MediaPickerViewModel@Inject constructor(
     override val app: Application,
) : TMVVMViewModel(app) {
}