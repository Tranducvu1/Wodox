package com.wodox.ui.task.taskdetail.description.optionmenu

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.wodox.core.extension.AbstractView
import com.wodox.core.extension.SpacesItemDecoration
import com.wodox.domain.docs.model.TextFormat
import com.wodox.home.R
import com.wodox.home.databinding.ItemDescriptionOptionMenuViewBinding
import com.wodox.ui.task.taskdetail.description.optionmenu.DescriptionOptionMenu.TextFormatMenuType
import com.wodox.home.BR


class DescriptionOptionMenuView(context: Context, attrs: AttributeSet?) :
    AbstractView(context, attrs) {

    interface OnItemClickListener : DescriptionOptionMenuItemAdapter.OnItemClickListener

    var listener: OnItemClickListener? = null

    override fun layoutId(): Int = R.layout.item_description_option_menu_view

    override fun viewBinding() = binding as ItemDescriptionOptionMenuViewBinding

    override fun viewInitialized() {
        val spacing = context.resources.getDimension(com.wodox.core.R.dimen.dp_10).toInt()
        viewBinding().apply {
            recyclerView.apply {
                adapter = DescriptionOptionMenuItemAdapter(
                    context,
                    object : OnItemClickListener {
                        override fun onClick(
                            menu: DescriptionOptionMenu,
                            view: View?
                        ) {
                            listener?.onClick(menu)
                        }
                    }
                )
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                addItemDecoration(SpacesItemDecoration(spacing, false))
            }

            setVariable(BR.menus, DescriptionOptionMenu.getDefaults())
            executePendingBindings()

        }
    }

    fun setColorFormat(color: Int) {
        val adapter = viewBinding().recyclerView.adapter as? DescriptionOptionMenuItemAdapter
        adapter?.updateSelectedColor(color)
    }

    fun setTextSize(size: Int) {
        val adapter = viewBinding().recyclerView.adapter as? DescriptionOptionMenuItemAdapter
        adapter?.updateTextSize(size)
    }

    fun setColorHighLight(color: Int) {
        val adapter = viewBinding().recyclerView.adapter as? DescriptionOptionMenuItemAdapter
        adapter?.updateHighLight(color)
    }

    fun updateTextFormat(textFormat: TextFormat) {
        val menuList = DescriptionOptionMenu.getDefaults().map { menu ->
            menu.copy(
                isSelected = when (menu.type) {
                    TextFormatMenuType.BOLD -> textFormat.isBold
                    TextFormatMenuType.ITALIC -> textFormat.isItalic
                    TextFormatMenuType.UNDERLINE -> textFormat.isUnderline
                    TextFormatMenuType.STRIKE_THROUGH -> textFormat.isStrikeThough
                    else -> false
                },
                size = if (menu.type == TextFormatMenuType.SIZE) textFormat.textSize.toInt() else menu.size,
                color = if (menu.type == TextFormatMenuType.COLOR) textFormat.getColor(context) else menu.color,
                colorHighLight = if (menu.type == TextFormatMenuType.HIGHLIGHT) textFormat.highLightColor
                    ?: menu.colorHighLight else menu.colorHighLight,
            )
        }
        val adapter = viewBinding().recyclerView.adapter as? DescriptionOptionMenuItemAdapter
        adapter?.updateListItems(menuList)
    }
}
