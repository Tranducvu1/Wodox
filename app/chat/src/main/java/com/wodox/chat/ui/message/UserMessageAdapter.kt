package com.wodox.chat.ui.message

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import com.wodox.chat.BR
import com.wodox.chat.databinding.ItemMessageFriendLayoutBinding
import com.wodox.chat.databinding.ItemMessageUserLayoutBinding
import com.wodox.domain.chat.model.local.MessageChat
import com.wodox.core.ui.adapter.TMVVMAdapter
import com.wodox.core.ui.adapter.TMVVMViewHolder

class UserMessageAdapter : TMVVMAdapter<MessageChat>(ArrayList()) {

    companion object {
        private const val VIEW_TYPE_USER = 1
        private const val VIEW_TYPE_FRIEND = 2
    }

    override fun getItemViewType(position: Int): Int {
        return if (list[position].isCurrentUser) VIEW_TYPE_USER else VIEW_TYPE_FRIEND
    }

    override fun onCreateViewHolderBase(
        parent: ViewGroup?,
        viewType: Int
    ): TMVVMViewHolder {
        return when (viewType) {
            VIEW_TYPE_USER -> {
                val binding = ItemMessageUserLayoutBinding.inflate(
                    LayoutInflater.from(parent?.context),
                    parent,
                    false
                )
                TMVVMViewHolder(binding)
            }

            else -> {
                val binding = ItemMessageFriendLayoutBinding.inflate(
                    LayoutInflater.from(parent?.context),
                    parent,
                    false
                )
                TMVVMViewHolder(binding)
            }
        }
    }

    override fun onBindViewHolderBase(
        holder: TMVVMViewHolder?,
        position: Int
    ) {
        val message = list.getOrNull(position) ?: return

        android.util.Log.d("UserMessageAdapter", "═══════════════════════════════")
        android.util.Log.d("UserMessageAdapter", "Binding position: $position")
        android.util.Log.d("UserMessageAdapter", "Message text: ${message.text}")
        android.util.Log.d("UserMessageAdapter", "Is current user: ${message.isCurrentUser}")

        val binding = holder?.binding as? ViewDataBinding

        when {
            message.isCurrentUser && binding is ItemMessageUserLayoutBinding -> {
                android.util.Log.d("UserMessageAdapter", "→ Binding to USER layout")
                binding.setVariable(BR.item, message)
                binding.executePendingBindings()
            }

            !message.isCurrentUser && binding is ItemMessageFriendLayoutBinding -> {
                android.util.Log.d("UserMessageAdapter", "→ Binding to FRIEND layout")
                binding.setVariable(BR.item, message)
                binding.executePendingBindings()
            }

            else -> {
                android.util.Log.e("UserMessageAdapter", "✗ BINDING MISMATCH!")
            }
        }
        android.util.Log.d("UserMessageAdapter", "═══════════════════════════════")
    }

    override fun submitList(messages: List<MessageChat>) {
        val oldList = ArrayList(list)
        list.clear()
        list.addAll(messages)
        val diffCallback = MessageDiffCallback(oldList, list)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        diffResult.dispatchUpdatesTo(this)
    }

    fun clearAll() {
        list.clear()
        notifyDataSetChanged()
    }

    fun cleanup() {
    }


    private class MessageDiffCallback(
        private val oldList: List<MessageChat>,
        private val newList: List<MessageChat>
    ) : DiffUtil.Callback() {

        override fun getOldListSize() = oldList.size

        override fun getNewListSize() = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldItem = oldList[oldItemPosition]
            val newItem = newList[newItemPosition]
            return oldItem.id == newItem.id ||
                    (oldItem.timestamp == newItem.timestamp && oldItem.text == newItem.text)
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }
    }
}