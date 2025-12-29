package com.wodox.ui.task.menu

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.wodox.core.ui.adapter.TMVVMAdapter
import com.wodox.core.ui.adapter.TMVVMViewHolder
import com.wodox.home.BR
import com.wodox.home.databinding.ItemTaskBarLayoutBinding

class TaskBarAdapter(private val context: Context, private val listener: OnItemClickListener) :
    TMVVMAdapter<TaskBarMenu>(ArrayList()) {
        interface OnItemClickListener {
        fun onClick(menu: TaskBarMenu)
    }

    override fun onCreateViewHolderBase(parent: ViewGroup?, viewType: Int): TMVVMViewHolder {
        val binding = ItemTaskBarLayoutBinding.inflate(LayoutInflater.from(parent?.context), parent, false)
        return TMVVMViewHolder(binding)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolderBase(holder: TMVVMViewHolder?, position: Int) {
        val menu = list[position]
        val binding = holder?.binding as ItemTaskBarLayoutBinding
        binding.clContainer.setOnClickListener {
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
        binding.setVariable(BR.menu, menu)
        binding.executePendingBindings()
    }
}