package com.wodox.common.view.colorpicker

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import com.wodox.common.BR
import com.wodox.common.databinding.ItemColorPickerLayoutBinding
import com.wodox.core.extension.debounceClick
import com.wodox.resources.R
import com.wodox.core.extension.getColorFromAttr
import com.wodox.core.extension.gone
import com.wodox.core.ui.adapter.TMVVMAdapter
import com.wodox.core.ui.adapter.TMVVMViewHolder

class ColorItemAdapter(
    val context: Context,
    var listener: OnItemClickListener?,
    var isEnabled: Boolean = true,
) :
    TMVVMAdapter<ColorItemPicker>(ArrayList<ColorItemPicker>()) {
    interface OnItemClickListener {
        fun onClick(color: ColorItemPicker)

        fun onClickMoreColor()
    }

    override fun onCreateViewHolderBase(
        parent: ViewGroup?,
        viewType: Int,
    ): TMVVMViewHolder {
        val binding = ItemColorPickerLayoutBinding.inflate(
            LayoutInflater.from(parent?.context),
            parent,
            false
        )

        return TMVVMViewHolder(binding)
    }

    @SuppressLint("CheckResult")
    override fun onBindViewHolderBase(holder: TMVVMViewHolder?, position: Int) {
        val color = list[position]
        val binding = holder?.binding as ItemColorPickerLayoutBinding

        binding.viewSelected.gone(!color.isSelected)
        binding.viewSelected.backgroundTintList = ColorStateList.valueOf(color.color)
        binding.shadowView.isShadowEnabled = !color.isSelected && !color.isNone

        binding.ivMoreColor.setBackgroundResource(
            if (color.isNone) {
                R.drawable.ic_none
            } else {
                R.drawable.ic_more_color
            }
        )
        binding.ivMoreColor.backgroundTintList = if (color.isNone) {
            ColorStateList.valueOf(context.getColorFromAttr(R.attr.primaryColor))
        } else {
            null
        }

        binding.viewColor.backgroundTintList = ColorStateList.valueOf(color.color)
        binding.ctContainer.debounceClick {
            if (!isEnabled) {
                return@debounceClick
            }
            if (color.isMore || color.isNone) {
                for ((index, item) in list.withIndex()) {
                    item.isSelected = false
                    notifyItemChanged(index)
                }
                if (color.isMore) {
                    listener?.onClickMoreColor()
                } else {
                    listener?.onClick(color)
                }
                return@debounceClick
            }

            if (color.isSelected) {
                return@debounceClick
            }

            val previousIndex = list.indexOfFirst { it.isSelected }
            val currentIndex = list.indexOfFirst { it.color == color.color }

            if (previousIndex != currentIndex) {
                for (item in list) {
                    item.isSelected = color.color == item.color
                }
                notifyItemChanged(previousIndex)
                notifyItemChanged(currentIndex)

                listener?.onClick(color)
            }
        }
        binding.setVariable(BR.color, color)
        binding.executePendingBindings()
    }
}