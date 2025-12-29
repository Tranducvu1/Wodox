package com.wodox.ui.task.taskdetail.description.fontbottomsheet

import android.app.Application
import android.os.Bundle
import androidx.lifecycle.MutableLiveData
import com.wodox.core.base.viewmodel.TMVVMViewModel
import com.wodox.core.extension.parcelable
import com.wodox.domain.docs.model.TextFormat
import com.wodox.model.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FontDescriptionViewModel @Inject constructor(
     application: Application
) : TMVVMViewModel(application) {
    var fonts = MutableLiveData(arrayListOf<TextFormat>())
    var selectedFont = MutableLiveData(
        TextFormat()
    )

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
        println("Font size: ${fonts.size}")
        fonts.forEach { println("Font: ${it.fontName}") }
        for (font in fonts) {
            font.isSelected = selectedFont.value?.fontName == font.fontName
        }
        this.fonts.postValue(fonts)
    }


}