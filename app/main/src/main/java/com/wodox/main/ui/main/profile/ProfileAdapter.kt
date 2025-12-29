package com.wodox.main.ui.main.profile

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.bumptech.glide.Glide
import com.wodox.domain.main.model.Item
import com.wodox.domain.main.model.ItemTypeProfile
import com.wodox.home.BR
import androidx.core.graphics.toColorInt
import com.wodox.core.ui.adapter.TMVVMAdapter
import com.wodox.core.ui.adapter.TMVVMViewHolder
import com.wodox.main.databinding.ItemProfileLayoutBinding


class ProfileAdapter(
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
        val binding = ItemProfileLayoutBinding.inflate(
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
        val binding = holder?.binding as ItemProfileLayoutBinding

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
                ItemTypeProfile.MY_CALENDAR -> {
                    if (item.uri != null) {
                        Glide.with(ctx)
                            .load(item.uri)
                            .centerCrop()
                            .into(imageView)
                    } else {
                        setIconWithColor(
                            imageView,
                            com.wodox.home.R.drawable.ic_calendar,
                            "#7C4DFF".toColorInt()
                        )
                    }
                }

                ItemTypeProfile.MUTE -> {
                    setIconWithColor(
                        imageView,
                        com.wodox.resources.R.drawable.ic_notification_mute,
                        "#00BFA5".toColorInt()
                    )
                }

                ItemTypeProfile.HELP -> {
                    setIconWithColor(
                        imageView,
                        com.wodox.resources.R.drawable.ic_help,
                        "#00BFA5".toColorInt()
                    )
                }

                ItemTypeProfile.SIGN_OUT -> {
                    setIconWithColor(
                        imageView,
                        com.wodox.resources.R.drawable.ic_logout,
                        "#2196F3".toColorInt()
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
