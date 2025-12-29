package com.wodox.chat.ui.chat

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.wodox.chat.BR
import com.wodox.chat.databinding.ItemActivityChatLayoutBinding
import com.wodox.domain.chat.model.local.ActivityItem
import com.wodox.core.ui.adapter.TMVVMAdapter
import com.wodox.core.ui.adapter.TMVVMViewHolder

class ActivityAdapter(
    private val context: Context?,
    private val listener: OnItemClickListener
) : TMVVMAdapter<ActivityItem>(ArrayList()) {

    interface OnItemClickListener {}

    override fun onCreateViewHolderBase(
        parent: ViewGroup?,
        viewType: Int
    ): TMVVMViewHolder {
        val binding =
            ItemActivityChatLayoutBinding.inflate(
                LayoutInflater.from(parent?.context),
                parent,
                false
            )
        return TMVVMViewHolder(binding)
    }

    override fun onBindViewHolderBase(holder: TMVVMViewHolder?, position: Int) {
        val task = list.getOrNull(position)
        val binding = holder?.binding as ItemActivityChatLayoutBinding

        binding.setVariable(BR.item, task)
        binding.executePendingBindings()
    }


}