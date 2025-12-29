package com.wodox.ui.task.taskdetail.description.optionmenu

import android.graphics.Color
import androidx.annotation.DrawableRes
import com.wodox.core.data.model.Selectable
import com.wodox.resources.R


data class DescriptionOptionMenu(
    @DrawableRes val resId: Int? = null,
    val type: TextFormatMenuType,
    var color: Int = 0,
    var colorHighLight: Int = 0,
    var size: Int = 0,
    override var isSelected: Boolean = false
) : Selectable {
    enum class TextFormatMenuType {
        FONT_FAMILY, COLOR, SIZE,
        BOLD, ITALIC, STRIKE_THROUGH,
        UNDERLINE, HIGHLIGHT,

    }

    companion object {
        fun getDefaults(): List<DescriptionOptionMenu> {
            val data = ArrayList<DescriptionOptionMenu>()
            data.add(DescriptionOptionMenu(R.drawable.ic_font, TextFormatMenuType.FONT_FAMILY))
            data.add(
                DescriptionOptionMenu(
                    R.drawable.ic_color_purple,
                    TextFormatMenuType.COLOR,
                    color = Color.BLACK.toInt()
                )
            )
            data.add(DescriptionOptionMenu(null, TextFormatMenuType.SIZE, size = 16))
            data.add(DescriptionOptionMenu(R.drawable.ic_bold, TextFormatMenuType.BOLD))
            data.add(DescriptionOptionMenu(R.drawable.ic_italic, TextFormatMenuType.ITALIC))
            data.add(
                DescriptionOptionMenu(
                    R.drawable.ic_text_strikethrough,
                    TextFormatMenuType.STRIKE_THROUGH
                )
            )
            data.add(
                DescriptionOptionMenu(
                    R.drawable.ic_text_underline,
                    TextFormatMenuType.UNDERLINE
                )
            )
            data.add(
                DescriptionOptionMenu(
                    R.drawable.ic_hightlight_selection,
                    TextFormatMenuType.HIGHLIGHT
                )
            )
            return data
        }
    }
}
