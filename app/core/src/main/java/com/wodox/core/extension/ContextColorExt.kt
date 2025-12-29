package com.wodox.core.extension

import android.R
import android.content.Context
import android.util.TypedValue
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat

@ColorInt
fun Context.getColorCompat(@ColorRes colorRes: Int): Int {
    return ContextCompat.getColor(this, colorRes)
}


@ColorInt
fun Context.getThemeColor(@AttrRes attrRes: Int, @ColorInt defaultColor: Int = 0): Int {
    val typedValue = TypedValue()
    return if (theme.resolveAttribute(attrRes, typedValue, true)) {
        typedValue.data
    } else {
        defaultColor
    }
}


val Context.primaryColor: Int
    @ColorInt
    get() {
        val customPrimary = getThemeAttrColor("primaryColor")
        if (customPrimary != 0) return customPrimary

        return getThemeColor(
            R.attr.colorPrimary,
            "#6200EE".color
        )
    }

val Context.secondaryColor: Int
    @ColorInt
    get() = getThemeColor(
        R.attr.colorSecondary,
        "#03DAC6".color // Material default secondary
    )


val Context.accentColor: Int
    @ColorInt
    get() = getThemeColor(
        R.attr.colorAccent,
        "#03DAC6".color
    )

/**
 * Get background color from theme
 */
val Context.backgroundColor: Int
    @ColorInt
    get() = getThemeColor(
        R.attr.colorBackground,
        "#FFFFFF".color
    )

/**
 * Get surface color from theme
 */
val Context.surfaceColor: Int
    @ColorInt
    get() = getThemeColor(
        R.attr.colorBackground,
        "#FFFFFF".color
    )

/**
 * Get error color from theme
 */
val Context.errorColor: Int
    @ColorInt
    get() = getThemeColor(
        R.attr.textColorPrimary, // Placeholder
        "#B00020".color // Material default error
    )

/**
 * Helper function để get color từ attribute name
 */
private fun Context.getThemeAttrColor(attrName: String): Int {
    val attrs = intArrayOf(
        resources.getIdentifier(attrName, "attr", packageName)
    )
    if (attrs[0] == 0) return 0

    val typedValue = TypedValue()
    return if (theme.resolveAttribute(attrs[0], typedValue, true)) {
        typedValue.data
    } else {
        0
    }
}

/**
 * Get color with alpha applied
 */
@ColorInt
fun Context.getColorWithAlpha(@ColorRes colorRes: Int, alpha: Float): Int {
    return getColorCompat(colorRes).withAlphaFloat(alpha)
}

/**
 * Get theme color with alpha applied
 */
@ColorInt
fun Context.getThemeColorWithAlpha(@AttrRes attrRes: Int, alpha: Float): Int {
    return getThemeColor(attrRes).withAlphaFloat(alpha)
}