package com.wodox.core.extension

import android.content.Context
import android.graphics.Color
import android.util.TypedValue
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.core.graphics.toColorInt
import androidx.fragment.app.FragmentManager


val String.color: Int
    @ColorInt
    get() = try {
        this.toColorInt()
    } catch (e: IllegalArgumentException) {
        Color.BLACK
    }

@ColorInt
fun Context.getColorFromAttr(@AttrRes attrRes: Int): Int {
    val typedValue = TypedValue()
    theme.resolveAttribute(attrRes, typedValue, true)
    return typedValue.data
}


fun Int.toHexColor(includeAlpha: Boolean = true): String {
    return if (includeAlpha) {
        String.format("#%08X", this)
    } else {
        String.format("#%06X", 0xFFFFFF and this)
    }
}

fun Int.adjustBrightness(factor: Float): Int {
    val alpha = Color.alpha(this)
    val red = (Color.red(this) * factor).toInt().coerceIn(0, 255)
    val green = (Color.green(this) * factor).toInt().coerceIn(0, 255)
    val blue = (Color.blue(this) * factor).toInt().coerceIn(0, 255)
    return Color.argb(alpha, red, green, blue)
}


fun Int.withAlpha(alpha: Int): Int {
    return Color.argb(
        alpha.coerceIn(0, 255),
        Color.red(this),
        Color.green(this),
        Color.blue(this)
    )
}

fun Int.withAlphaFloat(alpha: Float): Int {
    return withAlpha((alpha * 255).toInt())
}


fun Int.lighten(percentage: Float = 0.2f): Int {
    return adjustBrightness(1f + percentage)
}


fun Int.darken(percentage: Float = 0.2f): Int {
    return adjustBrightness(1f - percentage)
}

val Int.isDark: Boolean
    get() {
        val darkness =
            1 - (0.299 * Color.red(this) + 0.587 * Color.green(this) + 0.114 * Color.blue(this)) / 255
        return darkness >= 0.5
    }


val Int.contrastColor: Int
    get() = if (isDark) Color.WHITE else Color.BLACK

fun Int.rawColor(): String {
    return String.format("#%08X", this)
}