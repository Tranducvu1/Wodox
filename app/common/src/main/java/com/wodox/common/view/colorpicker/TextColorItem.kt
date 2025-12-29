package com.wodox.common.view.colorpicker

import com.wodox.core.extension.color
import com.wodox.core.extension.ensureValidColor
import com.wodox.core.extension.rawColor

data class TextColorItem(
    var colorString: String = "",
    override var isSelected: Boolean = false,
    override val isMore: Boolean = false,
    override val isNone: Boolean = false,
) : ColorItemPicker {

    override var color: Int = 0
        get() =  colorString.ensureValidColor().color
        set(value) {
            field = value
            colorString = value.rawColor()
        }
}