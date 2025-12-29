package com.onegravity.rteditor.spans

import android.content.Context
import android.graphics.Paint
import android.graphics.Typeface
import android.text.TextPaint
import android.text.style.TypefaceSpan

class CustomTypefaceSpan(
    val context: Context,
    val fontName: String,
    val style: Int = Typeface.NORMAL
) :
    TypefaceSpan("") {
    override fun updateDrawState(ds: TextPaint) {
        applyCustomTypeFace(ds)
    }

    override fun updateMeasureState(paint: TextPaint) {
        applyCustomTypeFace(paint)
    }

    private fun applyCustomTypeFace(paint: Paint) {
        when (style) {
            Typeface.BOLD -> {
                paint.isFakeBoldText = true
            }

            Typeface.ITALIC -> {
                paint.textSkewX = -0.25f
            }

            Typeface.BOLD_ITALIC -> {
                paint.isFakeBoldText = true
                paint.textSkewX = -0.25f
            }
        }
        paint.typeface = createAssetTypeface()
    }

    private fun createAssetTypeface(): Typeface {
        return try {
            Typeface.createFromAsset(context.assets, "fonts/$fontName.ttf")
        } catch (e: Exception) {
            e.printStackTrace()
            Typeface.DEFAULT
        }
    }
}