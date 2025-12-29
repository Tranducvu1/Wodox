package com.wodox.chat.ui.channelchat

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.wodox.chat.databinding.ItemChannelMemberBinding
import com.wodox.core.R
import com.wodox.domain.chat.model.ChannelMember
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

class ChannelMemberAdapter(
    private val onMemberClick: ((ChannelMember) -> Unit)? = null
) : ListAdapter<ChannelMember, ChannelMemberAdapter.MemberViewHolder>(MemberDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemberViewHolder {
        val binding = ItemChannelMemberBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MemberViewHolder(binding, onMemberClick)
    }

    override fun onBindViewHolder(holder: MemberViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class MemberViewHolder(
        private val binding: ItemChannelMemberBinding,
        private val onMemberClick: ((ChannelMember) -> Unit)?
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(member: ChannelMember) {
            binding.apply {
                tvMemberName.text = "User ${member.userId.toString().take(8)}"

                tvRole.text = member.role.name
                tvRole.setBackgroundColor(
                    when (member.role.name) {
                        "OWNER" -> ContextCompat.getColor(root.context, R.color.role_owner)
                        "ADMIN" -> ContextCompat.getColor(root.context, R.color.role_admin)
                        else -> ContextCompat.getColor(root.context, R.color.role_member)
                    }
                )

                tvJoinedDate.text = formatJoinedDate(member.joinedAt)

                root.setOnClickListener {
                    onMemberClick?.invoke(member)
                }
            }
        }

        private fun formatJoinedDate(timestamp: Long): String {
            val now = System.currentTimeMillis()
            val diff = now - timestamp

            return when {
                diff < TimeUnit.MINUTES.toMillis(1) -> "Joined just now"
                diff < TimeUnit.HOURS.toMillis(1) -> {
                    val minutes = TimeUnit.MILLISECONDS.toMinutes(diff)
                    "Joined $minutes minute${if (minutes > 1) "s" else ""} ago"
                }
                diff < TimeUnit.DAYS.toMillis(1) -> {
                    val hours = TimeUnit.MILLISECONDS.toHours(diff)
                    "Joined $hours hour${if (hours > 1) "s" else ""} ago"
                }
                diff < TimeUnit.DAYS.toMillis(7) -> {
                    val days = TimeUnit.MILLISECONDS.toDays(diff)
                    "Joined $days day${if (days > 1) "s" else ""} ago"
                }
                else -> {
                    val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                    "Joined ${sdf.format(Date(timestamp))}"
                }
            }
        }
    }

    private class MemberDiffCallback : DiffUtil.ItemCallback<ChannelMember>() {
        override fun areItemsTheSame(oldItem: ChannelMember, newItem: ChannelMember): Boolean {
            return oldItem.userId == newItem.userId && oldItem.channelId == newItem.channelId
        }

        override fun areContentsTheSame(oldItem: ChannelMember, newItem: ChannelMember): Boolean {
            return oldItem == newItem
        }
    }
}