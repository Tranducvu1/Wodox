package com.wodox.main.ui.main.topbar

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import com.wodox.core.extension.color
import com.wodox.core.extension.primaryColor
import com.wodox.core.ui.adapter.TMVVMAdapter
import com.wodox.core.ui.adapter.TMVVMViewHolder
import com.wodox.main.BR
import com.wodox.main.databinding.ItemTopBarLayoutBinding

class TopBarAdapter(private val context: Context, private val listener: OnItemClickListener) :
    TMVVMAdapter<TopBarMenu>(ArrayList()) {
    interface OnItemClickListener {
        fun onClick(menu: TopBarMenu)
    }

    override fun onCreateViewHolderBase(parent: ViewGroup?, viewType: Int): TMVVMViewHolder {
        val binding =
            ItemTopBarLayoutBinding.inflate(LayoutInflater.from(parent?.context), parent, false)
        return TMVVMViewHolder(binding)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolderBase(holder: TMVVMViewHolder?, position: Int) {
        val menu = list[position]
        val binding = holder?.binding as ItemTopBarLayoutBinding
        binding.ctContainer.setOnClickListener {
            val previousIndex = list.indexOfFirst { it.isSelected }
            val currentIndex = list.indexOfFirst { it.type == menu.type }

            if (previousIndex != currentIndex) {
                for (item in list) {
                    item.isSelected = item.type == menu.type
                }
                notifyItemChanged(previousIndex)
                notifyItemChanged(currentIndex)
            }
            listener.onClick(menu)
        }

        binding.tvTitle.setTextColor(
            if (menu.isSelected) {
                context.primaryColor
            } else {
                "#E1E0E0".color
            }
        )

        binding.root.setBackgroundColor(
            if (menu.isSelected) {
                "#D3DEB8".color
            } else {
                Color.TRANSPARENT
            }
        )

        binding.setVariable(BR.menu, menu)
        binding.executePendingBindings()
    }
}