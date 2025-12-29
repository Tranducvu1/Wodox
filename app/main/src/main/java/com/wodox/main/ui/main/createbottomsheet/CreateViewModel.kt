package com.wodox.main.ui.main.createbottomsheet

import android.app.Application
import com.wodox.core.base.viewmodel.TMVVMViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CreateViewModel @Inject constructor(
     override val app: Application
) : TMVVMViewModel(app) {

}