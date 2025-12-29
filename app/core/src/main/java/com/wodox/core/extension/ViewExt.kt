package com.wodox.core.extension

import android.view.View
import android.view.ViewTreeObserver

private var lastClickTime = 0L

fun View.debounceClick(delay: Long = 500, action: (View) -> Unit) {
    setOnClickListener { view ->
        val now = System.currentTimeMillis()
        if (now - lastClickTime >= delay) {
            lastClickTime = now
            action(view)
        }
    }
}

fun View.show(isShow: Boolean) {
    visibility = if (isShow) View.VISIBLE else View.GONE
}

fun View.show() {
    visibility = View.VISIBLE
}

fun View.gone(isEmpty: Boolean) {
    visibility = if (isEmpty) View.GONE else View.VISIBLE
}

fun View.gone() {
    visibility = View.GONE
}

fun View.onGlobalLayout(action: () -> Unit) {
    viewTreeObserver.addOnGlobalLayoutListener(
        object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                viewTreeObserver.removeOnGlobalLayoutListener(this)
                action.invoke()
            }
        }
    )
}

data class ViewSize(
    val width: Int,
    val height: Int
)

fun View.getSizeOfView(): ViewSize {
    return ViewSize(
        width = this.measuredWidth,
        height = this.measuredHeight
    )
}

val View.viewWidth: Int
    get() = this.measuredWidth

val View.viewHeight: Int
    get() = this.measuredHeight

val View.height: Int
    get() = this.measuredHeight

fun View.post(action: () -> Unit) {
    this.post(Runnable { action() })
}