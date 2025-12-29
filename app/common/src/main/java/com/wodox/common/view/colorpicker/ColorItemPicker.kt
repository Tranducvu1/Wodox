package com.wodox.common.view.colorpicker

import com.wodox.core.data.model.Selectable


interface ColorItemPicker: Selectable {
    var color: Int
    val isMore: Boolean
    val isNone: Boolean
}