package com.wodox.common.view.colorpicker

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.PopupWindow
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.wodox.common.BR
import com.wodox.common.R
import com.wodox.common.databinding.ItemColorPickerViewBinding
import com.wodox.common.extension.showMoreColor
import com.wodox.core.extension.AbstractView
import com.wodox.core.extension.getSizeOfView
import com.wodox.core.extension.onGlobalLayout
import com.wodox.core.extension.px
import kotlin.math.min

class ColorPickerView(context: Context, attrs: AttributeSet?) : AbstractView(context, attrs) {

    interface OnItemClickListener {
        fun onClick(color: ColorItemPicker)
    }

    var colors: List<ColorItemPicker> = ArrayList()
        set(value) {
            field = value
            onGlobalLayout {
                bindData()
            }
        }

    var listener: ColorItemAdapter.OnItemClickListener? = null
        set(value) {
            field = value
            (viewBinding().recyclerView.adapter as? ColorItemAdapter)?.listener = listener
        }

    var isColorEnabled: Boolean = true
        set(value) {
            field = value
            (viewBinding().recyclerView.adapter as? ColorItemAdapter)?.isEnabled = value
        }

    override fun layoutId(): Int = R.layout.item_color_picker_view

    override fun viewBinding() = binding as ItemColorPickerViewBinding

    override fun viewInitialized() {
        val space = context.resources.getDimension(com.wodox.core.R.dimen.dp_8).toInt()
        with(viewBinding()) {
            recyclerView.adapter = ColorItemAdapter(context, listener)

//            recyclerView.addDecoration(SpacesItemDecoration(space, false))
        }

        onGlobalLayout {
            bindData()
        }
    }

    private fun bindData() {
        if (colors.isNotEmpty()) {
            val itemWith = (width - 24.px) / colors.size

            viewBinding().recyclerView.apply {
                layoutManager =
                    object : LinearLayoutManager(context, HORIZONTAL, false) {
                        override fun checkLayoutParams(lp: RecyclerView.LayoutParams?): Boolean {
                            lp?.width = itemWith
                            return true
                        }
                    }
            }

            viewBinding().setVariable(BR.colors, colors)
            viewBinding().executePendingBindings()
        }
    }

    companion object {
        fun show(
            context: Context,
            view: View,
            colors: ArrayList<ColorItemPicker>,
            listener: OnItemClickListener,
            width: Int = 350.px,
            background: Drawable? = null,
            onMoreColorPickerChangeState: ((popupWindow: PopupWindow, isOpen: Boolean, isUserCancel: Boolean) -> Unit)? = null
        ) {
            val popupView = ColorPickerView(view.context, null)

            popupView.colors = colors
            val popupWindow = PopupWindow(
                popupView,
                width,
                WindowManager.LayoutParams.WRAP_CONTENT,
                false
            ).apply {
                isOutsideTouchable = true
            }

            popupView.listener = object : ColorItemAdapter.OnItemClickListener {
                override fun onClick(color: ColorItemPicker) {
                    listener.onClick(color)
                    popupWindow.dismiss()
                }

                override fun onClickMoreColor() {
                    onMoreColorPickerChangeState?.invoke(popupWindow, true, false)
                    context.showMoreColor(success = {
                        val color = colors.firstOrNull { it.isMore } ?: return@showMoreColor

                        color.color = it
                        color.isSelected = true

                        listener.onClick(color)

                        onMoreColorPickerChangeState?.invoke(popupWindow, false, false)
                    }, cancel = {
                        onMoreColorPickerChangeState?.invoke(popupWindow, false, true)
                    })
                }
            }

            popupWindow.elevation = 0.px.toFloat()
            popupWindow.isClippingEnabled = false
            popupWindow.setBackgroundDrawable(
                background ?: ResourcesCompat.getDrawable(
                    popupView.context.resources,
                    com.wodox.resources.R.drawable.bg_popup_window,
                    popupView.context.theme
                )
            )

            val location = IntArray(2)
            view.getLocationOnScreen(location)
            val locationX = min(location[0] + (view.width - popupWindow.width) / 2, 16.px)

            // Show popup window first
            popupWindow.showAtLocation(
                view,
                Gravity.NO_GRAVITY,
                locationX,
                location[1] - view.height - 16.px
            )

            popupView.onGlobalLayout {
                val size = popupView.getSizeOfView()
                popupWindow.update(
                    min(location[0] + (view.width - popupWindow.width) / 2, 16.px),
                    location[1] - size.height - 16.px,
                    -1,
                    -1,
                    true
                )
            }
        }
    }
}