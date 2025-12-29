package com.wodox.chat.ui.chat

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.wodox.core.extension.debounceClick
import com.wodox.chat.BR
import com.wodox.chat.databinding.ItemNotificationLayoutBinding
import com.wodox.domain.chat.model.local.Notification
import com.wodox.core.ui.adapter.TMVVMAdapter
import com.wodox.core.ui.adapter.TMVVMViewHolder

class NotificationAdapter(
    private val context: Context?,
    private val listener: OnItemClickListener
) : TMVVMAdapter<Notification>(ArrayList()) {
    interface OnItemClickListener {
        fun onViewTask(notification: Notification)
        fun onMarkDone(notification: Notification)
        fun onDismiss(notification: Notification)
    }

    override fun onCreateViewHolderBase(parent: ViewGroup?, viewType: Int): TMVVMViewHolder {
        val binding = ItemNotificationLayoutBinding.inflate(
            LayoutInflater.from(parent?.context),
            parent,
            false
        )
        return TMVVMViewHolder(binding)
    }

    override fun onBindViewHolderBase(holder: TMVVMViewHolder?, position: Int) {
        val item = list.getOrNull(position) ?: return
        val binding = holder?.binding as ItemNotificationLayoutBinding
        binding.setVariable(BR.notification, item)

        binding.tvViewTask.debounceClick {
            listener.onViewTask(item)
        }

        binding.tvDone.debounceClick {
            listener.onMarkDone(item)
        }

        binding.tvDismiss.debounceClick {
            listener.onDismiss(item)
        }

        binding.executePendingBindings()
    }
}