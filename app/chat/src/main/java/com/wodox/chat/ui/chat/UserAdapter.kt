package com.wodox.chat.ui.chat

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.wodox.core.extension.debounceClick
import com.wodox.core.extension.show
import com.wodox.chat.BR
import com.wodox.chat.databinding.ItemUserLayoutBinding
import com.wodox.domain.chat.model.UserWithFriendStatus
import com.wodox.core.ui.adapter.TMVVMAdapter
import com.wodox.core.ui.adapter.TMVVMViewHolder
import com.wodox.domain.home.model.local.FriendStatus

class UserAdapter(
    private val context: Context?,
    private val listener: OnItemClickListener
) : TMVVMAdapter<UserWithFriendStatus>(ArrayList()) {

    interface OnItemClickListener {
        fun onAccept(item: UserWithFriendStatus)
        fun onReject(item: UserWithFriendStatus)

        fun onChat(item: UserWithFriendStatus)
    }

    override fun onCreateViewHolderBase(parent: ViewGroup?, viewType: Int): TMVVMViewHolder {
        val binding =
            ItemUserLayoutBinding.inflate(
                LayoutInflater.from(parent?.context),
                parent,
                false
            )
        return TMVVMViewHolder(binding)
    }

    override fun onBindViewHolderBase(holder: TMVVMViewHolder?, position: Int) {
        val item = list.getOrNull(position) ?: return
        val binding = holder?.binding as ItemUserLayoutBinding
        val isSender = item.relationUserId == item.currentUserId
        val isReceiver = item.relationFriendId == item.currentUserId

        when (item.status) {

            FriendStatus.PENDING -> {
                when {
                    isReceiver -> {
                        binding.ivAccept.show(true)
                        binding.ivReject.show(true)
                    }

                    isSender -> {
                        binding.ivAccept.show(false)
                        binding.ivReject.show(true)
                    }

                    else -> {
                        binding.ivAccept.show(false)
                        binding.ivReject.show(false)
                    }
                }
            }

            else -> {
                binding.ivAccept.show(false)
                binding.ivReject.show(false)
            }
        }

        binding.ivAccept.debounceClick {
            listener.onAccept(item)
        }

        binding.ivReject.debounceClick {
            listener.onReject(item)
        }
        binding.clContainer.debounceClick {
            listener.onChat(item)
        }

        binding.setVariable(BR.user, item.user)
        binding.executePendingBindings()
    }
}