package com.wodox.ui.task.optioncreate

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.graphics.toColorInt
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.wodox.core.ui.adapter.TMVVMAdapter
import com.wodox.core.ui.adapter.TMVVMViewHolder
import com.wodox.domain.home.model.local.Item
import com.wodox.domain.home.model.local.ItemType
import com.wodox.home.BR
import com.wodox.home.R
import com.wodox.home.databinding.ItemItemsLayoutBinding

class ItemAdapter(
    private val context: Context?, private val listener: OnItemClickListener
) : TMVVMAdapter<Item>(ArrayList()) {

    interface OnItemClickListener {
        fun onClick(item: Item)
    }

    override fun onCreateViewHolderBase(
        parent: ViewGroup?, viewType: Int
    ): TMVVMViewHolder {
        val binding = ItemItemsLayoutBinding.inflate(
            LayoutInflater.from(parent?.context), parent, false
        )
        return TMVVMViewHolder(binding)
    }

    override fun onBindViewHolderBase(
        holder: TMVVMViewHolder?, position: Int
    ) {
        val item = list.getOrNull(position) ?: return
        val binding = holder?.binding as? ItemItemsLayoutBinding ?: return

        setupItemIcon(binding.ivAttachmentIcon, item)
        setupClickListener(binding, item)

        binding.setVariable(BR.itemTask, item)
        binding.executePendingBindings()
    }

    private fun setupItemIcon(imageView: ImageView, item: Item) {
        context?.let { ctx ->
            when (item.type) {
                ItemType.TASK -> {
                    if (item.uri != null) {
                        loadImageWithGlide(imageView, item.uri!!)
                    } else {
                        setTintedIcon(
                            imageView, R.drawable.ic_check_circle, "#4CAF50"
                        )
                    }
                }

                ItemType.CHANNEL -> {
                    setTintedIcon(
                        imageView, R.drawable.ic_hashtag, "#2196F3"
                    )
                }

                ItemType.REMINDER -> {
                    setTintedIcon(
                        imageView, R.drawable.ic_reminder, "#FF9800"
                    )
                }

                ItemType.DOC -> {
                    setTintedIcon(
                        imageView, com.wodox.resources.R.drawable.ic_docs, "#9C27B0"
                    )
                }

                else -> {
                    setTintedIcon(
                        imageView, com.wodox.resources.R.drawable.ic_add, "#757575"
                    )
                }
            }
        }
    }

    private fun loadImageWithGlide(imageView: ImageView, uri: Any) {
        context?.let { ctx ->
            val cornerRadius =
                ctx.resources.getDimensionPixelSize(com.wodox.resources.R.dimen.dp_12)
            Glide.with(ctx).load(uri)
                .apply(RequestOptions().transform(RoundedCorners(cornerRadius))).centerCrop()
                .into(imageView)
        }
    }

    private fun setTintedIcon(imageView: ImageView, drawableRes: Int, colorHex: String) {
        context?.let { ctx ->
            val drawable = ContextCompat.getDrawable(ctx, drawableRes)?.mutate()
            drawable?.let {
                DrawableCompat.setTint(it, colorHex.toColorInt())
                imageView.setImageDrawable(it)
            }
        }
    }

    private fun setupClickListener(binding: ItemItemsLayoutBinding, item: Item) {
        binding.root.setOnClickListener {
            listener.onClick(item)
        }
    }
}