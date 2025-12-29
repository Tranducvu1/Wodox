package com.wodox.common.view.colorpicker

import android.content.Context
import android.graphics.Paint
import android.graphics.Typeface
import android.text.TextPaint
import android.text.style.TypefaceSpan
import com.wodox.domain.docs.model.TypeFaces

class CustomTypefaceSpan(
    val context: Context,
    var fontName: String,
    var style: Int = Typeface.NORMAL
) :
    TypefaceSpan("") {
    override fun updateDrawState(ds: TextPaint) {
        applyCustomTypeFace(ds)
    }

    override fun updateMeasureState(paint: TextPaint) {
        applyCustomTypeFace(paint)
    }

    private fun applyCustomTypeFace(paint: Paint) {
        val tf = getTypeFace(context)
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
        paint.typeface = tf
    }

    private fun getTypeFace(context: Context): Typeface {
        if (fontName.isEmpty()) {
            return Typeface.DEFAULT
        }

        return TypeFaces.getTypeFace(context.applicationContext, "fonts/$fontName.ttf")
            ?: Typeface.DEFAULT
    }
}