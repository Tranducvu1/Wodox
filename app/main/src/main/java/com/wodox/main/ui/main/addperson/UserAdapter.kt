package com.wodox.main.ui.main.addperson

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.wodox.core.ui.adapter.TMVVMAdapter
import com.wodox.core.ui.adapter.TMVVMViewHolder
import com.wodox.domain.user.model.User
import com.wodox.home.BR
import com.wodox.main.databinding.ItemUserAdapterBinding

class UserAdapter(
    private val context: Context?,
    private val listener: OnItemClickListener
) : TMVVMAdapter<User>(ArrayList()) {

    interface OnItemClickListener {
        fun onClick(user: User)
    }

    override fun onCreateViewHolderBase(
        parent: ViewGroup?,
        viewType: Int
    ): TMVVMViewHolder {
        val binding = ItemUserAdapterBinding.inflate(
            LayoutInflater.from(parent?.context),
            parent,
            false
        )
        return TMVVMViewHolder(binding)
    }

    override fun onBindViewHolderBase(
        holder: TMVVMViewHolder?,
        position: Int
    ) {
        val user = list.getOrNull(position) ?: return
        val binding = holder?.binding as ItemUserAdapterBinding

        binding.tvAction.setOnClickListener {
            listener.onClick(user)
        }

        binding.setVariable(BR.user, user)
        binding.executePendingBindings()
    }

    override fun submitList(newList: List<User>) {
        list.clear()
        list.addAll(newList)
        notifyDataSetChanged()
    }

}