package com.wodox.common.view.colorpicker

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.widget.PopupWindow
import androidx.core.content.res.ResourcesCompat
import com.wodox.common.R
import com.wodox.common.databinding.ItemTextSizePopUpViewBinding
import com.wodox.core.extension.AbstractView
import com.wodox.core.extension.px

class TextSizePopupView(context: Context, attrs: AttributeSet?) :
    AbstractView(context, attrs) {

    interface OnItemClickListener {
        fun onScrollStop(size: Int)
    }

    var listener: OnItemClickListener? = null

    var size: Int = 16
        set(value) {
            field = value
            if (isAttachedToWindow) {
                viewBinding().numberPicker.value = value
            }
        }

    override fun layoutId(): Int = R.layout.item_text_size_pop_up_view

    override fun viewBinding(): ItemTextSizePopUpViewBinding =
        binding as ItemTextSizePopUpViewBinding

    override fun viewInitialized() {
        this@TextSizePopupView.post { setupAction() }
    }

    private fun setupAction() {
        viewBinding().numberPicker.apply {
            isAccessibilityDescriptionEnabled = false
            setTypeface(context.resources.getFont(com.wodox.resources.R.font.baloo2_bold))
            setSelectedTypeface(context.resources.getFont(com.wodox.resources.R.font.baloo2_medium))

            setTextColor(context.getColor(com.wodox.resources.R.color.blackAlpha60))
            setSelectedTextColor(context.getColor(com.wodox.resources.R.color.blackAlpha60))

            value = size
            setOnScrollListener { _, scrollState ->
                if (scrollState == com.shawnlin.numberpicker.NumberPicker.OnScrollListener.SCROLL_STATE_IDLE) {
                    listener?.onScrollStop(value)
                }
            }
        }
    }

    companion object {
        fun show(anchor: View,onSizeSelected : (Int) -> Unit) {
            val popupView = TextSizePopupView(anchor.context, null)

            val popupWindow = PopupWindow(
                popupView,
                100.px,
                180.px,
                true
            ).apply {
                isOutsideTouchable = true
                elevation = 16.px.toFloat()
                isClippingEnabled = true
                setBackgroundDrawable(
                    ResourcesCompat.getDrawable(
                        popupView.context.resources,
                        com.wodox.resources.R.drawable.bg_popup_window,
                        popupView.context.theme
                    )
                )
            }


            popupView.listener = object : OnItemClickListener {
                override fun onScrollStop(size: Int) {
                    onSizeSelected(size)
                    popupWindow.dismiss()
                }
            }

            val location = IntArray(2)
            anchor.getLocationOnScreen(location)

            popupWindow.showAtLocation(
                anchor,
                Gravity.NO_GRAVITY,
                location[0] + anchor.width / 2 - popupWindow.width / 2,
                location[1] + anchor.height + 8.px
            )
        }
    }
}
