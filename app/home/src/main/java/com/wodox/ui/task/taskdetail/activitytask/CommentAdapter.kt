package com.wodox.ui.task.taskdetail.activitytask

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.wodox.core.extension.show
import com.wodox.core.ui.adapter.TMVVMAdapter
import com.wodox.core.ui.adapter.TMVVMViewHolder
import com.wodox.domain.home.model.local.Comment
import com.wodox.home.BR
import com.wodox.home.databinding.ItemCommentLayoutBinding
import java.util.UUID

class CommentAdapter(
    private val context: Context?,
    private val listener: OnCommentActionListener? = null
) : TMVVMAdapter<Comment>(ArrayList()) {

    private var editingCommentId: UUID? = null

    interface OnCommentActionListener {
        fun onCommentDelete(comment: Comment)
        fun onCommentEdit(comment: Comment)
        fun onCommentLike(comment: Comment)
        fun onCommentReply(comment: Comment)
        fun onMoreOptions(comment: Comment)
    }

    fun setEditingCommentId(id: UUID?) {
        val oldId = editingCommentId
        editingCommentId = id

        if (oldId != null) {
            val oldPosition = list.indexOfFirst { it.id == oldId }
            if (oldPosition != -1) notifyItemChanged(oldPosition)
        }
        if (id != null) {
            val newPosition = list.indexOfFirst { it.id == id }
            if (newPosition != -1) notifyItemChanged(newPosition)
        }
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

    override fun onBindViewHolderBase(holder: TMVVMViewHolder?, position: Int) {
        val comment = list.getOrNull(position) ?: return
        val binding = holder?.binding as ItemCommentLayoutBinding
        val isEditing = comment.id == editingCommentId
        context?.let { ctx ->
            if (isEditing) {
                binding.root.setBackgroundColor(
                    ContextCompat.getColor(ctx, com.wodox.resources.R.color.color_editing_highlight)
                )
            } else {
                binding.root.setBackgroundColor(
                    ContextCompat.getColor(ctx, android.R.color.transparent)
                )
            }

            if (isEditing) {
                binding.tvCommentContent.backgroundTintList =
                    ContextCompat.getColorStateList(
                        ctx,
                        com.wodox.resources.R.color.color_editing_bubble
                    )
            } else {
                binding.tvCommentContent.backgroundTintList =
                    ContextCompat.getColorStateList(ctx, com.wodox.resources.R.color.colorF5F5F5)
            }
        }

        binding.tvEditingIndicator.show(isEditing)

        binding.tvLike.setOnClickListener {
            listener?.onCommentLike(comment)
        }

        binding.tvReply.setOnClickListener {
            listener?.onCommentReply(comment)
        }

        binding.tvEdit.setOnClickListener {
            listener?.onCommentEdit(comment)
        }

        binding.ivMoreOptions.setOnClickListener {
            listener?.onMoreOptions(comment)
        }

        binding.llCommentContent.setOnLongClickListener {
            listener?.onCommentEdit(comment)
            true
        }
        binding.setVariable(BR.item, comment)
        binding.executePendingBindings()
    }
}