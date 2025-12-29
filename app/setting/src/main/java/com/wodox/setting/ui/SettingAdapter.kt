package com.wodox.setting.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.wodox.core.extension.show
import com.wodox.core.ui.adapter.TMVVMViewHolder
import com.wodox.setting.BR
import com.wodox.setting.databinding.ItemSettingBinding
import com.wodox.setting.databinding.ItemSettingSectionHeaderBinding
import com.wodox.setting.model.SettingItem
import com.wodox.setting.model.SettingSection

class SettingAdapter(
    private val context: Context?,
    private val listener: OnItemClickListener
) : ListAdapter<Any, RecyclerView.ViewHolder>(SettingDiffCallback()) {

    interface OnItemClickListener {
        fun onClick(item: SettingItem)
    }

    companion object {
        private const val VIEW_TYPE_HEADER = 0
        private const val VIEW_TYPE_ITEM = 1
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is SettingSection -> VIEW_TYPE_HEADER
            is SettingItem -> VIEW_TYPE_ITEM
            else -> throw IllegalArgumentException("Unknown item type")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_HEADER -> {
                val binding = ItemSettingSectionHeaderBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                TMVVMViewHolder(binding)
            }
            VIEW_TYPE_ITEM -> {
                val binding = ItemSettingBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                TMVVMViewHolder(binding)
            }
            else -> throw IllegalArgumentException("Unknown view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)

        when (holder) {
            is TMVVMViewHolder -> {
                when (item) {
                    is SettingSection -> {
                        val binding = holder.binding as ItemSettingSectionHeaderBinding
                        binding.setVariable(BR.section, item)
                        binding.executePendingBindings()
                    }
                    is SettingItem -> {
                        val binding = holder.binding as ItemSettingBinding
                        binding.setVariable(BR.item, item)
                        binding.executePendingBindings()

                        // Handle badge visibility
                        binding.tvBadge.show(!item.badge.isNullOrEmpty())

                        // Handle click
                        binding.root.setOnClickListener {
                            listener.onClick(item)
                        }
                    }
                }
            }
        }
    }

    override fun submitList(list: List<Any>?) {
        val flatList = mutableListOf<Any>()
        list?.forEach { item ->
            if (item is SettingSection) {
                flatList.add(item)
                flatList.addAll(item.items)
            }
        }
        super.submitList(flatList)
    }

    class SettingDiffCallback : DiffUtil.ItemCallback<Any>() {
        override fun areItemsTheSame(oldItem: Any, newItem: Any): Boolean {
            return when {
                oldItem is SettingSection && newItem is SettingSection ->
                    oldItem.title == newItem.title
                oldItem is SettingItem && newItem is SettingItem ->
                    oldItem.type == newItem.type
                else -> false
            }
        }

        override fun areContentsTheSame(oldItem: Any, newItem: Any): Boolean {
            return oldItem == newItem
        }
    }
}