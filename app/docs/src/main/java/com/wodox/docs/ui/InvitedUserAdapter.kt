package com.wodox.docs.ui

import com.wodox.domain.docs.model.model.InvitedUser


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.wodox.docs.databinding.ItemInvitedUserBinding

class InvitedUserAdapter(
    private var users: List<InvitedUser> = emptyList(),
    private val onRemoveClick: (InvitedUser) -> Unit
) : RecyclerView.Adapter<InvitedUserAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemInvitedUserBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding, onRemoveClick)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(users[position])
    }

    override fun getItemCount() = users.size

    fun updateUsers(newUsers: List<InvitedUser>) {
        users = newUsers
        notifyDataSetChanged()
    }

    class ViewHolder(
        private val binding: ItemInvitedUserBinding,
        private val onRemoveClick: (InvitedUser) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(user: InvitedUser) {
            binding.apply {
                tvUserName.text = user.userName
                tvPermission.text = when (user.permission.name) {
                    "EDIT" -> "Có thể chỉnh sửa"
                    else -> "Chỉ xem"
                }

                ivRemove.setOnClickListener {
                    onRemoveClick(user)
                }
            }
        }
    }
}
