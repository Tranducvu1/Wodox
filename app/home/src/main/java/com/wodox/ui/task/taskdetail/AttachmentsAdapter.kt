package com.wodox.ui.task.taskdetail

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.wodox.core.ui.adapter.TMVVMAdapter
import com.wodox.core.ui.adapter.TMVVMViewHolder
import com.wodox.domain.home.model.local.Attachment
import com.wodox.domain.home.model.local.AttachmentType
import com.wodox.home.BR
import com.wodox.home.databinding.ItemAttachmentLayoutBinding

class AttachmentsAdapter(
    private val context: Context?,
    private val listener: OnItemClickListener
) : TMVVMAdapter<Attachment>(ArrayList()) {
    interface OnItemClickListener {
        fun onAttachmentClick(attachment: Attachment)
        fun onDeleteClick(attachment: Attachment)
    }

    override fun onCreateViewHolderBase(
        parent: ViewGroup?,
        viewType: Int
    ): TMVVMViewHolder {
        val binding = ItemAttachmentLayoutBinding.inflate(
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
        val attachment = list.getOrNull(position) ?: return
        val binding = holder?.binding as ItemAttachmentLayoutBinding

        setAttachmentIcon(binding.ivAttachmentIcon, attachment)

        binding.root.setOnClickListener {
            listener.onAttachmentClick(attachment)
        }

        binding.ivDelete.setOnClickListener {
            listener.onDeleteClick(attachment)
        }
        binding.setVariable(BR.item, attachment)
        binding.executePendingBindings()
    }

    private fun setAttachmentIcon(imageView: ImageView, attachment: Attachment) {
        context?.let { ctx ->
            when (attachment.type) {
                AttachmentType.IMAGE -> {
                    if (attachment.uri != null) {
                        Glide.with(ctx)
                            .load(attachment.uri)
                            .centerCrop()
                            .into(imageView)
                    } else {
                        imageView.setImageDrawable(
                            ContextCompat.getDrawable(ctx, com.wodox.resources.R.drawable.ic_add)
                        )
                    }
                }

                AttachmentType.VIDEO -> {
                    imageView.setImageDrawable(
                        ContextCompat.getDrawable(ctx, com.wodox.resources.R.drawable.ic_add)
                    )
                }

                AttachmentType.FILE -> {
                    imageView.setImageDrawable(
                        ContextCompat.getDrawable(ctx, com.wodox.resources.R.drawable.ic_add)
                    )
                }

                AttachmentType.AUDIO -> {
                    imageView.setImageDrawable(
                        ContextCompat.getDrawable(ctx, com.wodox.resources.R.drawable.ic_add)
                    )
                }

                else -> {
                    imageView.setImageDrawable(
                        ContextCompat.getDrawable(ctx, com.wodox.resources.R.drawable.ic_add)
                    )
                }
            }
        }
    }

}