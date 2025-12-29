package com.wodox.core.base.adapter

import android.graphics.drawable.Drawable
import android.net.Uri
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.wodox.core.ui.adapter.TMVVMAdapter
import java.util.Date
import java.util.concurrent.TimeUnit
import androidx.core.graphics.toColorInt
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target

object CommonBindingAdapters {
    @BindingAdapter("android:visibility", "invisibleType", requireAll = false)
    @JvmStatic
    fun setVisibilityWithType(view: View, isVisible: Boolean, invisibleType: String?) {
        view.visibility = when {
            isVisible -> View.VISIBLE
            invisibleType?.lowercase() == "invisible" -> View.INVISIBLE
            else -> View.GONE
        }
    }

    @BindingAdapter("android:visibility")
    @JvmStatic
    fun setVisibilityInt(view: View, visibility: Int?) {
        view.visibility = visibility ?: View.GONE
    }

    @BindingAdapter("android:selected")
    @JvmStatic
    fun setSelected(view: View, isSelected: Boolean) {
        view.isSelected = isSelected
    }

    @BindingAdapter("android:enabled")
    @JvmStatic
    fun setEnabled(view: View, isEnabled: Boolean) {
        view.isEnabled = isEnabled
    }

    @BindingAdapter("android:alpha")
    @JvmStatic
    fun setAlpha(view: View, alpha: Float) {
        view.alpha = alpha
    }

    @BindingAdapter("android:background")
    @JvmStatic
    fun setBackground(view: View, drawableId: Int?) {
        if (drawableId != null && drawableId != 0) {
            view.setBackgroundResource(drawableId)
        }
    }

    @BindingAdapter("app:items")
    @JvmStatic
    fun <T> setRecyclerViewItems(recyclerView: RecyclerView, items: List<T>?) {
        @Suppress("UNCHECKED_CAST") val adapter = recyclerView.adapter as? TMVVMAdapter<T>
        adapter?.submitList(items ?: emptyList())
    }

    @BindingAdapter("android:src")
    @JvmStatic
    fun setImageResource(view: ImageView, resId: Int?) {
        resId?.let { view.setImageResource(it) }
    }

    @BindingAdapter("iconRes", "tintSelected", requireAll = true)
    @JvmStatic
    fun setIconWithTint(view: ImageView, iconRes: Int, isSelected: Boolean) {
        view.setImageResource(iconRes)
        val color = if (isSelected) {
            ContextCompat.getColor(view.context, com.wodox.core.R.color.colorPrimary)
        } else {
            ContextCompat.getColor(view.context, com.wodox.core.R.color.black)
        }
        view.setColorFilter(color)
    }

    @BindingAdapter("isVisible")
    @JvmStatic
    fun setIsVisible(view: View, visible: Boolean?) {
        view.visibility = if (visible == true) View.VISIBLE else View.INVISIBLE
    }

    @BindingAdapter("isGone")
    @JvmStatic
    fun setIsGone(view: View, gone: Boolean?) {
        view.visibility = if (gone == true) View.GONE else View.VISIBLE
    }

    @BindingAdapter("isVisibleOrGone")
    @JvmStatic
    fun setIsVisibleOrGone(view: View, isVisible: Boolean?) {
        view.visibility = if (isVisible == true) View.VISIBLE else View.GONE
    }

    @BindingAdapter("app:isSelected")
    @JvmStatic
    fun setIsSelected(view: View, isSelected: Boolean?) {
        view.isSelected = isSelected ?: false
    }

    @JvmStatic
    @BindingAdapter("android:text")
    fun bindLogTime(textView: TextView, date: Date?) {
        if (date == null) {
            textView.text = ""
            return
        }
        val now = System.currentTimeMillis()
        val diff = now - date.time

        val minutes = TimeUnit.MILLISECONDS.toMinutes(diff)
        val hours = TimeUnit.MILLISECONDS.toHours(diff)
        val days = TimeUnit.MILLISECONDS.toDays(diff)

        textView.text = when {
            minutes < 1 -> "Just now"
            minutes < 60 -> "$minutes minutes ago"
            hours < 24 -> "$hours hours ago"
            else -> "$days days ago"
        }
    }

    @JvmStatic
    @BindingAdapter("app:avatarColor")
    fun setAvatarColor(imageView: ImageView, userId: java.util.UUID?) {
        val avatarColor = when (userId?.hashCode()?.rem(5)) {
            0 -> "#FF6B6B".toColorInt()
            1 -> "#4ECDC4".toColorInt()
            2 -> "#45B7D1".toColorInt()
            3 -> "#FFA07A".toColorInt()
            else -> "#98D8C8".toColorInt()
        }
        imageView.setBackgroundColor(avatarColor)
    }

    @JvmStatic
    @BindingAdapter("chatMessage")
    fun setChatMessage(textView: TextView, message: String?) {
        android.util.Log.d("ChatBinding", "Message: $message")

        if (message.isNullOrEmpty()) {
            textView.visibility = View.GONE
            return
        }

        // Kiểm tra nếu là image message
        if (message.startsWith("[Image]")) {
            android.util.Log.d("ChatBinding", "Detected image message")
            textView.visibility = View.GONE

            val parent = textView.parent as? ViewGroup
            val imageView = parent?.findViewById<ImageView>(
                textView.context.resources.getIdentifier(
                    "ivMessageImage", "id", textView.context.packageName
                )
            )

            // Lấy URI từ message
            val uriString = message.replace("[Image]", "").trim()

            android.util.Log.d("ChatBinding", "URI: $uriString")
            android.util.Log.d("ChatBinding", "ImageView found: ${imageView != null}")

            if (uriString.isNotEmpty() && imageView != null) {
                imageView.visibility = View.VISIBLE
                loadImage(imageView, uriString)
            }
        } else if (message.startsWith("[File]")) {
            // Xử lý file message
            textView.visibility = View.VISIBLE
            textView.text = message.replace("[File]", "📎 ")

            val parent = textView.parent as? ViewGroup
            val imageView = parent?.findViewById<ImageView>(
                textView.context.resources.getIdentifier(
                    "ivMessageImage", "id", textView.context.packageName
                )
            )
            imageView?.visibility = View.GONE
        } else {
            // Message thông thường
            textView.visibility = View.VISIBLE
            textView.text = message

            val parent = textView.parent as? ViewGroup
            val imageView = parent?.findViewById<ImageView>(
                textView.context.resources.getIdentifier(
                    "ivMessageImage", "id", textView.context.packageName
                )
            )
            imageView?.visibility = View.GONE
        }
    }

    private fun loadImage(imageView: ImageView, uriString: String) {
        android.util.Log.d("ChatBinding", "Loading image: $uriString")

        Glide.with(imageView.context)
            .load(Uri.parse(uriString))
            .apply(
                RequestOptions()
                    .transform(RoundedCorners(24))
                    .placeholder(android.R.drawable.ic_menu_gallery)
                    .error(android.R.drawable.ic_menu_close_clear_cancel)
                    .override(800, 800) // Giới hạn kích thước
            )
            .listener(object : com.bumptech.glide.request.RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable?>,
                    isFirstResource: Boolean
                ): Boolean {
                    android.util.Log.e("ChatBinding", "Failed to load image: ${e?.message}")
                    e?.logRootCauses("ChatBinding")
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable,
                    model: Any,
                    target: Target<Drawable?>?,
                    dataSource: DataSource,
                    isFirstResource: Boolean
                ): Boolean {
                    android.util.Log.d("ChatBinding", "Image loaded successfully")
                    return false
                }
            })
            .into(imageView)
    }


    @JvmStatic
    @BindingAdapter("imageMessage")
    fun setImageMessage(imageView: ImageView, message: String?) {
        android.util.Log.d("ImageBinding", "Message: $message")

        if (message.isNullOrEmpty() || !message.startsWith("[Image]")) {
            imageView.visibility = View.GONE
            return
        }

        val uriString = message.replace("[Image]", "").trim()
        android.util.Log.d("ImageBinding", "Loading URI: $uriString")

        if (uriString.isEmpty()) {
            imageView.visibility = View.GONE
            return
        }

        imageView.visibility = View.VISIBLE

        Glide.with(imageView.context)
            .load(Uri.parse(uriString))
            .apply(
                RequestOptions()
                    .transform(RoundedCorners(24))
                    .placeholder(android.R.drawable.ic_menu_gallery)
                    .error(android.R.drawable.ic_menu_close_clear_cancel)
                    .override(800, 800)
            )
            .listener(object : com.bumptech.glide.request.RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable?>,
                    isFirstResource: Boolean
                ): Boolean {
                    android.util.Log.e("ImageBinding", "Failed: ${e?.message}")
                    e?.logRootCauses("ImageBinding")
                    imageView.visibility = View.GONE
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable,
                    model: Any,
                    target: Target<Drawable?>?,
                    dataSource: DataSource,
                    isFirstResource: Boolean
                ): Boolean {
                    android.util.Log.d("ImageBinding", "Image loaded successfully")
                    return false
                }
            })
            .into(imageView)
    }


}