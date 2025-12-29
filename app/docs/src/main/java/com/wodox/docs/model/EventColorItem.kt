package com.wodox.docs.model

import com.wodox.common.view.colorpicker.ColorItemPicker
import com.wodox.core.extension.color

data class EventColorItem(
    var colorString: String,
    override var isSelected: Boolean = false,
    override val isMore: Boolean = false,
    override val isNone: Boolean = false
) : ColorItemPicker {

    override var color: Int = 0
        get() = colorString.color
        set(value) {
            field = value
            colorString = "#%x".format(value)
        }

    companion object {
        fun getDefaults(selectedColor: String? = null): List<EventColorItem> {
            val data = ArrayList<EventColorItem>()
            data.add(
                EventColorItem(
                    colorString = "#A1BA6B",
                    isMore = true
                )
            )

            if (!selectedColor.isNullOrEmpty()) {
                data.forEach {
                    it.isSelected = selectedColor == it.colorString
                }
            } else {
                data.firstOrNull()?.isSelected = true
            }

            return data
        }
    }
}

