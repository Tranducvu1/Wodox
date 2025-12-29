package com.wodox.chat.ui.channelchat

import android.view.LayoutInflater
import android.view.ViewGroup
import com.wodox.chat.BR
import com.wodox.chat.databinding.ItemChannelMessageOtherBinding
import com.wodox.chat.databinding.ItemChannelMessageUserBinding
import com.wodox.core.ui.adapter.TMVVMAdapter
import com.wodox.core.ui.adapter.TMVVMViewHolder
import com.wodox.domain.chat.model.ChannelMessage
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class ChannelMessageAdapter(
    private val currentUserId: UUID
) : TMVVMAdapter<ChannelMessage>(ArrayList()) {

    companion object {
        private const val VIEW_TYPE_USER = 1
        private const val VIEW_TYPE_OTHER = 2
    }

    override fun getItemViewType(position: Int): Int {
        return if (list[position].senderId == currentUserId) {
            VIEW_TYPE_USER
        } else {
            VIEW_TYPE_OTHER
        }
    }

    override fun onCreateViewHolderBase(
        parent: ViewGroup?,
        viewType: Int
    ): TMVVMViewHolder {
        return when (viewType) {
            VIEW_TYPE_USER -> {
                val binding = ItemChannelMessageUserBinding.inflate(
                    LayoutInflater.from(parent?.context),
                    parent,
                    false
                )
                TMVVMViewHolder(binding)
            }
            else -> {
                val binding = ItemChannelMessageOtherBinding.inflate(
                    LayoutInflater.from(parent?.context),
                    parent,
                    false
                )
                TMVVMViewHolder(binding)
            }
        }
    }

    override fun onBindViewHolderBase(holder: TMVVMViewHolder?, position: Int) {
        val message = list.getOrNull(position) ?: return
        val binding = holder?.binding

        when (binding) {
            is ItemChannelMessageUserBinding -> {
                binding.setVariable(BR.message, message)

                binding.tvTime.text = formatTime(message.timestamp)

                binding.executePendingBindings()
            }
            is ItemChannelMessageOtherBinding -> {
                binding.setVariable(BR.message, message)
                val firstLetter = message.senderName.firstOrNull()?.toString()?.uppercase() ?: "?"
                binding.tvAvatarLetter.text = firstLetter
                binding.tvAvatarLetter.setBackgroundColor(getColorForUser(message.senderId))
                binding.tvTime.text = formatTime(message.timestamp)

                binding.executePendingBindings()
            }
        }
    }

    override fun submitList(messages: List<ChannelMessage>) {
        list.clear()
        list.addAll(messages)
        notifyDataSetChanged()
    }

    private fun formatTime(timestamp: Long): String {
        val now = System.currentTimeMillis()
        val diff = now - timestamp

        return when {
            diff < 60_000 -> "Just now"
            diff < 3600_000 -> "${diff / 60_000}m ago"
            diff < 86400_000 -> SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(timestamp))
            diff < 604800_000 -> SimpleDateFormat("EEE HH:mm", Locale.getDefault()).format(Date(timestamp))
            else -> SimpleDateFormat("dd/MM HH:mm", Locale.getDefault()).format(Date(timestamp))
        }
    }

    private fun getColorForUser(userId: UUID): Int {
        val colors = listOf(
            0xFF6B6B.toInt(), // Red
            0x4ECDC4.toInt(), // Turquoise
            0x45B7D1.toInt(), // Blue
            0xFFA07A.toInt(), // Light Salmon
            0x98D8C8.toInt(), // Mint
            0xF06292.toInt(), // Pink
            0x9575CD.toInt(), // Purple
            0x4DB6AC.toInt()  // Teal
        )

        val index = userId.hashCode().rem(colors.size).let { if (it < 0) it + colors.size else it }
        return colors[index]
    }
}