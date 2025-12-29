package com.wodox.common.view.colorpicker

import androidx.databinding.BindingAdapter

object ColorPickerBindingAdapter {
    @BindingAdapter(value = ["colors"])
    @JvmStatic
    fun setColors(colorView: ColorPickerView, colors: ArrayList<ColorItemPicker>? = null) {
        colors?.let {
            colorView.colors = it
        }
    }
}