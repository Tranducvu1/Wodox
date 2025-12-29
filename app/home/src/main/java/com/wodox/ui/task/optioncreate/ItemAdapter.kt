package com.wodox.ui.task.optioncreate

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.graphics.toColorInt
import com.bumptech.glide.Glide
import com.wodox.core.ui.adapter.TMVVMAdapter
import com.wodox.core.ui.adapter.TMVVMViewHolder
import com.wodox.domain.home.model.local.Item
import com.wodox.domain.home.model.local.ItemType
import com.wodox.home.BR
import com.wodox.home.R
import com.wodox.home.databinding.ItemItemsLayoutBinding


class ItemAdapter(
    private val context: Context?,
    private val listener: OnItemClickListener
) : TMVVMAdapter<Item>(ArrayList()) {

    interface OnItemClickListener {
        fun onClick(item: Item)
    }

    override fun onCreateViewHolderBase(
        parent: ViewGroup?,
        viewType: Int
    ): TMVVMViewHolder {
        val binding = ItemItemsLayoutBinding.inflate(
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
        val items = list.getOrNull(position) ?: return
        val binding = holder?.binding as ItemItemsLayoutBinding

        setAttachmentIcon(binding.ivAttachmentIcon, items)

        binding.root.setOnClickListener {
            listener.onClick(items)
        }

        binding.setVariable(BR.itemTask, items)
        binding.executePendingBindings()
    }

    private fun setAttachmentIcon(imageView: ImageView, item: Item) {
        context?.let { ctx ->
            when (item.type) {
                ItemType.TASK -> {
                    if (item.uri != null) {
                        Glide.with(ctx)
                            .load(item.uri)
                            .centerCrop()
                            .into(imageView)
                    } else {
                        setIconWithColor(
                            imageView,
                            R.drawable.ic_check_circle,
                            "#4CAF50".toColorInt()
                        )
                    }
                }

                ItemType.CHANNEL -> {
                    setIconWithColor(
                        imageView,
                        R.drawable.ic_hashtag,
                        "#2196F3".toColorInt()
                    )
                }


                ItemType.REMINDER -> {
                    setIconWithColor(
                        imageView,
                        R.drawable.ic_reminder,
                        "#F44336".toColorInt()
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

    private fun setIconWithColor(imageView: ImageView, drawableRes: Int, color: Int) {
        context?.let { ctx ->
            val drawable = ContextCompat.getDrawable(ctx, drawableRes)?.mutate()
            drawable?.let {
                DrawableCompat.setTint(it, color)
                imageView.setImageDrawable(it)
            }
        }
    }
}