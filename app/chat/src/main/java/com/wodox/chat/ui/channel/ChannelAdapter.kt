package com.wodox.chat.ui.channel

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import com.wodox.chat.BR
import com.wodox.chat.databinding.ItemChannelBinding
import com.wodox.core.ui.adapter.TMVVMAdapter
import com.wodox.core.ui.adapter.TMVVMViewHolder
import com.wodox.domain.chat.model.Channel
import java.text.SimpleDateFormat
import java.util.Locale

class ChannelAdapter(
    private val onChannelClick: (Channel) -> Unit,
    private val onJoinClick: ((Channel) -> Unit)? = null
) : TMVVMAdapter<Channel>(ArrayList()) {

    override fun onCreateViewHolderBase(
        parent: ViewGroup?,
        viewType: Int
    ): TMVVMViewHolder {
        val binding = ItemChannelBinding.inflate(
            LayoutInflater.from(parent?.context),
            parent,
            false
        )
        return TMVVMViewHolder(binding)
    }

    override fun onBindViewHolderBase(holder: TMVVMViewHolder?, position: Int) {
        val channel = list.getOrNull(position) ?: return
        val binding = holder?.binding as ItemChannelBinding

        binding.root.setOnClickListener {
            onChannelClick(channel)
        }

        setupJoinButton(binding, channel)

        channel.lastMessageTime?.let { timestamp ->
            binding.tvTime.text = formatTime(timestamp)
        }

        binding.setVariable(BR.channel, channel)
        binding.executePendingBindings()
    }

    private fun setupJoinButton(binding: ItemChannelBinding, channel: Channel) {
        if (channel.isJoined) {
            // Already joined - show "Joined" button (disabled style)
            binding.btnJoin.text = "Joined"
            binding.btnJoin.backgroundTintList = ColorStateList.valueOf(0xFFE8F5E9.toInt())
            binding.btnJoin.setTextColor(0xFF4CAF50.toInt())
            binding.btnJoin.isEnabled = false
            binding.btnJoin.setOnClickListener(null)
        } else {
            // Not joined yet - show "Join" button
            binding.btnJoin.text = "Join"
            binding.btnJoin.backgroundTintList = ColorStateList.valueOf(0xFF007AFF.toInt())
            binding.btnJoin.setTextColor(0xFFFFFFFF.toInt())
            binding.btnJoin.isEnabled = true

            // Click to join
            binding.btnJoin.setOnClickListener {
                onJoinClick?.invoke(channel)
            }
        }
    }

    override fun submitList(newList: List<Channel>) {
        list.clear()
        list.addAll(newList)
        notifyDataSetChanged()
    }

    private fun formatTime(timestamp: Long): String {
        val now = System.currentTimeMillis()
        val diff = now - timestamp

        return when {
            diff < 60_000 -> "Just now"
            diff < 3600_000 -> "${diff / 60_000}m ago"
            diff < 86400_000 -> SimpleDateFormat("HH:mm", Locale.getDefault()).format(timestamp)
            diff < 604800_000 -> SimpleDateFormat("EEE", Locale.getDefault()).format(timestamp)
            else -> SimpleDateFormat("dd/MM", Locale.getDefault()).format(timestamp)
        }
    }
}