package com.wodox.ui.task.taskdetail.activitytask

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.wodox.core.ui.adapter.TMVVMAdapter
import com.wodox.core.ui.adapter.TMVVMViewHolder
import com.wodox.domain.home.model.local.Comment
import com.wodox.home.BR
import com.wodox.home.databinding.ItemCommentLayoutBinding

class CommentAdapter(
    private val context: Context?,
    private val listener: OnCommentActionListener? = null
) : TMVVMAdapter<Comment>(ArrayList()) {

    interface OnCommentActionListener {
        fun onCommentDelete(comment: Comment)
        fun onCommentLike(comment: Comment)
        fun onCommentReply(comment: Comment)
        fun onMoreOptions(comment: Comment)
    }

    override fun onCreateViewHolderBase(
        parent: ViewGroup?,
        viewType: Int
    ): TMVVMViewHolder {
        val binding = ItemCommentLayoutBinding.inflate(
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
        val comment = list.getOrNull(position) ?: return
        val binding = holder?.binding as ItemCommentLayoutBinding
        binding.setVariable(BR.item, comment)
        binding.executePendingBindings()

        binding.tvLike.setOnClickListener {
            listener?.onCommentLike(comment)
        }

        binding.tvReply.setOnClickListener {
            listener?.onCommentReply(comment)
        }

        binding.ivMoreOptions.setOnClickListener {
            listener?.onMoreOptions(comment)
        }
    }
}