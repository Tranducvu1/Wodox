package com.wodox.common.ui.menuview

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import com.wodox.common.BR
import com.wodox.common.databinding.ItemMenuOptionLayoutBinding
import com.wodox.core.extension.AppSwitch
import com.wodox.core.extension.debounceClick
import com.wodox.core.extension.px
import com.wodox.core.ui.adapter.TMVVMAdapter
import com.wodox.core.ui.adapter.TMVVMViewHolder
import kotlin.text.get

class MenuOptionAdapter(var listener: MenuOptionListener) : TMVVMAdapter<MenuOption>(ArrayList()) {

    override fun onCreateViewHolderBase(parent: ViewGroup?, viewType: Int): TMVVMViewHolder {
        val binding = ItemMenuOptionLayoutBinding.inflate(
            LayoutInflater.from(parent?.context),
            parent,
            false
        )

        return TMVVMViewHolder(binding)
    }

    override fun onBindViewHolderBase(holder: TMVVMViewHolder?, position: Int) {
        val menu = list.getOrNull(position) ?: return
        val binding = holder?.binding as ItemMenuOptionLayoutBinding

        binding.apply {
            llContainer.setPadding(
                16.px,
                if (position == 0) 12.px else 8.px,
                16.px,
                if (position == list.lastIndex) 12.px else 8.px
            )

            if (!menu.toggleEnabled) {
                llContainer.debounceClick {
                    listener.onClick(menu)
                }
            } else {
                sc.listener = object : AppSwitch.OnCheckedChangeListener {
                    override fun onChecked(view: AppSwitch, isChecked: Boolean) {
                        listener.onCheck(menu, isChecked)
                    }
                }
            }

            if (menu.tintColor != null) {
                ivIcon.imageTintList = ColorStateList.valueOf(menu.tintColor)
            }

            setVariable(BR.menu, menu)
            executePendingBindings()
        }
    }

    fun updateMenuTitle(type: MenuOptionType, newTitleResId: Int) {
        val index = list.indexOfFirst { it.type == type }
        if (index != -1) {
            val oldItem = list[index]
            list[index] = oldItem.copy(nameResId = newTitleResId)
            notifyItemChanged(index)
        }
    }

}
