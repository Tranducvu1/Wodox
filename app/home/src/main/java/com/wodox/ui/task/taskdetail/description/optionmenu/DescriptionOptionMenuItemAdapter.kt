package com.wodox.ui.task.taskdetail.description.optionmenu

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.LayerDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.graphics.toColorInt
import com.wodox.core.extension.titleTextColor
import com.wodox.core.extension.debounceClick
import com.wodox.core.extension.gone
import com.wodox.core.extension.show
import com.wodox.core.ui.adapter.TMVVMAdapter
import com.wodox.core.ui.adapter.TMVVMViewHolder
import com.wodox.home.databinding.ItemDescriptionMenuOptionLayoutBinding
import com.wodox.home.BR

class DescriptionOptionMenuItemAdapter(
    val context: Context,
    val listener: OnItemClickListener
) : TMVVMAdapter<DescriptionOptionMenu>(ArrayList()) {

    interface OnItemClickListener {
        fun onClick(menu: DescriptionOptionMenu, view: View? = null)
    }

    override fun onCreateViewHolderBase(parent: ViewGroup?, viewType: Int): TMVVMViewHolder {
        val binding = ItemDescriptionMenuOptionLayoutBinding.inflate(
            LayoutInflater.from(parent?.context),
            parent,
            false
        )
        return TMVVMViewHolder(binding)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolderBase(holder: TMVVMViewHolder?, position: Int) {
        val menu = list[position]
        val binding = holder?.binding as ItemDescriptionMenuOptionLayoutBinding

        menu.resId?.let { binding.ivIcon.setImageResource(it) }
        binding.ivIcon.isSelected = menu.isSelected
        binding.ivIcon.imageTintList =
            AppCompatResources.getColorStateList(context, com.wodox.resources.R.color.color_bg_menu_option)

        if (menu.type == DescriptionOptionMenu.TextFormatMenuType.SIZE) {
            binding.ivIcon.gone()
            binding.tvTextSize.show()
        }

        if (menu.type == DescriptionOptionMenu.TextFormatMenuType.COLOR) {
            binding.ivIcon.imageTintList = ColorStateList.valueOf(menu.color)
        }

        if (menu.type == DescriptionOptionMenu.TextFormatMenuType.SIZE) {
            binding.tvTextSize.text = menu.size.toString()
        }

        if (menu.type == DescriptionOptionMenu.TextFormatMenuType.HIGHLIGHT) {
            (binding.ivIcon.drawable as? LayerDrawable)?.getDrawable(0)?.let { drawable ->
                DrawableCompat.setTint(drawable, "#A3815A".toColorInt())
            }
            (binding.ivIcon.drawable as? LayerDrawable)?.getDrawable(1)?.let { drawable ->
                DrawableCompat.setTint(
                    drawable,
                    if (menu.colorHighLight != 0) menu.colorHighLight else context.titleTextColor()
                )
            }
        }

        binding.tvTextSize.debounceClick {
            listener.onClick(menu, binding.root)
        }

        binding.ivIcon.debounceClick {
            listener.onClick(menu, binding.root)
            when (menu.type) {
                DescriptionOptionMenu.TextFormatMenuType.BOLD, DescriptionOptionMenu.TextFormatMenuType.ITALIC, DescriptionOptionMenu.TextFormatMenuType.STRIKE_THROUGH, DescriptionOptionMenu.TextFormatMenuType.UNDERLINE -> {
                    menu.isSelected = !menu.isSelected
                }

                else -> {}
            }
            notifyItemChanged(position)
        }

        binding.setVariable(BR.menu, menu)
        binding.executePendingBindings()
    }

    fun updateSelectedColor(color: Int) {
        val position = list.indexOfFirst { it.type == DescriptionOptionMenu.TextFormatMenuType.COLOR }
        if (position != -1) {
            list[position] = list[position].copy(color = color)
            notifyItemChanged(position)
        }
    }

    fun updateTextSize(size: Int) {
        val position = list.indexOfFirst { it.type == DescriptionOptionMenu.TextFormatMenuType.SIZE }
        if (position != -1) {
            list[position] = list[position].copy(size = size)
            notifyItemChanged(position)
        }
    }

    fun updateHighLight(colorHighLight: Int) {
        val position = list.indexOfFirst { it.type == DescriptionOptionMenu.TextFormatMenuType.HIGHLIGHT }
        if (position != -1) {
            list[position] = list[position].copy(colorHighLight = colorHighLight)
            notifyItemChanged(position)
        }
    }

    override fun onViewRecycled(holder: TMVVMViewHolder) {
        super.onViewRecycled(holder)

        val binding = holder.binding as? ItemDescriptionMenuOptionLayoutBinding ?: return

        binding.apply {
            ivIcon.isSelected = false
            ivIcon.imageTintList =
                AppCompatResources.getColorStateList(context, com.wodox.resources.R.color.color_bg_menu_option)
            ivIcon.show()
            tvTextSize.gone()
        }
    }


    fun updateListItems(newList: List<DescriptionOptionMenu>) {
        list.clear()
        list.addAll(newList)
        notifyDataSetChanged()
    }

}
