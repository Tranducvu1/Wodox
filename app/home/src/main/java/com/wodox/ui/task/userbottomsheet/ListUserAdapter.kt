package com.wodox.ui.task.userbottomsheet

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.wodox.core.extension.debounceClick
import com.wodox.domain.chat.model.UserWithFriendStatus
import com.wodox.core.ui.adapter.TMVVMAdapter
import com.wodox.core.ui.adapter.TMVVMViewHolder
import com.wodox.home.BR
import com.wodox.home.databinding.IitemUserAssignLayoutBinding

class ListUserAdapter(
    private val context: Context?,
    private val listener: OnItemClickListener
) : TMVVMAdapter<UserWithFriendStatus>(ArrayList()) {

    interface OnItemClickListener {
        fun onClick(item: UserWithFriendStatus)
    }

    override fun onCreateViewHolderBase(parent: ViewGroup?, viewType: Int): TMVVMViewHolder {
        val binding =
            IitemUserAssignLayoutBinding.inflate(
                LayoutInflater.from(parent?.context),
                parent,
                false
            )
        return TMVVMViewHolder(binding)
    }

    override fun onBindViewHolderBase(holder: TMVVMViewHolder?, position: Int) {
        val item = list.getOrNull(position) ?: return
        val binding = holder?.binding as IitemUserAssignLayoutBinding


        binding.root.debounceClick {
            listener.onClick(item)
        }

        binding.setVariable(BR.user, item.user)
        binding.executePendingBindings()
    }
}