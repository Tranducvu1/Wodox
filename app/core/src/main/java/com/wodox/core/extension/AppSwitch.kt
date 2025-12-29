package com.wodox.core.extension

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.appcompat.widget.SwitchCompat
import androidx.databinding.BindingAdapter

class AppSwitch @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val switchCompat: SwitchCompat = SwitchCompat(context, attrs, defStyleAttr).apply {
        layoutParams = LayoutParams(
            LayoutParams.WRAP_CONTENT,
            LayoutParams.WRAP_CONTENT
        )
    }

    var listener: OnCheckedChangeListener? = null

    var isChecked: Boolean
        get() = switchCompat.isChecked
        set(value) {
            switchCompat.isChecked = value
        }

    init {
        addView(switchCompat)
        switchCompat.setOnCheckedChangeListener { _, isChecked ->
            listener?.onChecked(this@AppSwitch, isChecked)
        }
    }

    interface OnCheckedChangeListener {
        fun onChecked(view: AppSwitch, isChecked: Boolean)
    }

    fun toggle() {
        switchCompat.toggle()
    }

    override fun setEnabled(enabled: Boolean) {
        super.setEnabled(enabled)
        switchCompat.isEnabled = enabled
    }
}

@BindingAdapter("app:checked")
fun AppSwitch.setCheckedBinding(checked: Boolean?) {
    checked?.let {
        if (this.isChecked != it) {
            this.isChecked = it
        }
    }
}

@BindingAdapter("app:onCheckedChange")
fun AppSwitch.setOnCheckedChangeListener(listener: AppSwitch.OnCheckedChangeListener?) {
    this.listener = listener
}