package com.wodox.ui.task.menuoption

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.graphics.toColorInt
import com.wodox.core.ui.adapter.TMVVMAdapter
import com.wodox.core.ui.adapter.TMVVMViewHolder
import com.wodox.domain.home.model.local.ItemMenu
import com.wodox.domain.home.model.local.MenuOption
import com.wodox.home.BR
import com.wodox.home.databinding.ItemTaskMenuOptionLayoutBinding

class MenuOptionAdapter(
    private val context: Context?,
    private val listener: OnItemClickListener
) : TMVVMAdapter<MenuOption>(ArrayList()) {

    interface OnItemClickListener {
        fun onClick(menuOption: MenuOption)
    }

    override fun onCreateViewHolderBase(
        parent: ViewGroup?,
        viewType: Int
    ): TMVVMViewHolder {
        val binding = ItemTaskMenuOptionLayoutBinding.inflate(
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
        val menuOption = list.getOrNull(position) ?: return
        val binding = holder?.binding as ItemTaskMenuOptionLayoutBinding

        setAttachmentIcon(binding.ivAttachmentIcon, menuOption)

        binding.root.setOnClickListener {
            listener.onClick(menuOption)
        }

        binding.setVariable(BR.itemMenu, menuOption)
        binding.executePendingBindings()
    }

    private fun setAttachmentIcon(imageView: ImageView, item: MenuOption) {
        context?.let { ctx ->
            when (item.type) {
                ItemMenu.DUPLICATE -> {
                    setIconWithColor(
                        imageView,
                        com.wodox.home.R.drawable.ic_hashtag,
                        "#00BFA5".toColorInt()
                    )
                }

                ItemMenu.SHARE -> {
                    setIconWithColor(
                        imageView,
                        com.wodox.home.R.drawable.ic_chat,
                        "#00BFA5".toColorInt()
                    )
                }

                ItemMenu.REMIND -> {
                    setIconWithColor(
                        imageView,
                        com.wodox.home.R.drawable.ic_document,
                        "#2196F3".toColorInt()
                    )
                }

                ItemMenu.DELETE -> {
                    setIconWithColor(
                        imageView,
                        com.wodox.resources.R.drawable.ic_delete_draw,
                        "#FF3D00".toColorInt()
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
