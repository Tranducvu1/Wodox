package com.wodox.core.extension

import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import androidx.core.graphics.toColorInt
import androidx.core.graphics.toColorInt
data class Bounds(
    val left: Int = 0,
    val top: Int = 0,
    val right: Int = 0,
    val bottom: Int = 0
) {
    fun width(): Int = right - left
    fun height(): Int = bottom - top
}

fun String.bounds(paint: Paint): Bounds {
    val rect = Rect()
    paint.getTextBounds(this, 0, this.length, rect)
    return Bounds(
        left = rect.left,
        top = rect.top,
        right = rect.right,
        bottom = rect.bottom
    )
}


fun String.indexesOf(substring: String): List<Int> {
    if (substring.isEmpty()) return emptyList()

    val indexes = mutableListOf<Int>()
    var startIndex = 0

    while (true) {
        val index = this.indexOf(substring, startIndex)
        if (index == -1) break
        indexes.add(index)
        startIndex = index + 1
    }

    return indexes
}

fun String.ensureValidColor(): String {
    return try {
        this.toColorInt()
        this
    } catch (e: IllegalArgumentException) {
        Color.BLACK.toHexString()
    } catch (e: Exception) {
        Color.BLACK.toHexString()
    }
}

fun Int.toHexString(): String {
    return String.format("#%08X", this)
}