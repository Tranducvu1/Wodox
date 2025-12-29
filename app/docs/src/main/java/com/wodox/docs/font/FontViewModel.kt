package com.wodox.docs.font

import android.app.Application
import android.os.Bundle
import androidx.lifecycle.MutableLiveData
import com.wodox.core.base.viewmodel.TMVVMViewModel
import com.wodox.core.extension.parcelable
import com.wodox.docs.model.Constants
import com.wodox.domain.docs.model.TextFormat
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FontViewModel @Inject constructor(
    application: Application
) : TMVVMViewModel(application) {
    var fonts = MutableLiveData(arrayListOf<TextFormat>())
    var selectedFont = MutableLiveData(TextFormat())
    var isFirstTime: Boolean = true

    override fun onReceiveData(data: Bundle?) {
        super.onReceiveData(data)
        data?.parcelable<TextFormat>(Constants.Intents.TEXT_FORMAT)?.let {
            selectedFont.value = it
        }
    }

    override fun onCreate() {
        super.onCreate()
        loadFonts()
    }

    fun loadFonts() {
        val fonts = TextFormat.getDefaults()
        for (font in fonts) {
            font.isSelected = selectedFont.value?.fontName == font.fontName
        }
        this.fonts.postValue(fonts)
    }
}
