package com.wodox.common.view.colorpicker

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.os.Parcel
import android.text.Layout
import android.text.ParcelableSpan
import android.text.SpannableStringBuilder
import android.text.SpannedString
import android.text.style.LeadingMarginSpan
import com.wodox.resources.R
import com.wodox.core.extension.bounds
import com.wodox.core.extension.getColorFromAttr
import com.wodox.core.extension.indexesOf

class CustomLeadingMarginSpan(
    val context: Context,
    val leading: Int,
    val bulletGapWidth: Int,
    val bulletRadius: Int = 0,
    val style: Int = BULLET
) : LeadingMarginSpan, ParcelableSpan {
    private var paint: Paint? = null

    companion object {
        const val BULLET = 0
        const val NUMERIC = 1
    }

    override fun getLeadingMargin(first: Boolean): Int {
        if (first) {
            return when (this.style) {
                BULLET -> {
                    leading + bulletGapWidth + bulletRadius * 2
                }

                else -> {
                    val text = "99.".bounds(paint ?: Paint())
                    leading + bulletGapWidth + text.width()
                }
            }
        }
        return 0
    }

    override fun drawLeadingMargin(
        c: Canvas?,
        p: Paint?,
        x: Int,
        dir: Int,
        top: Int,
        baseline: Int,
        bottom: Int,
        text: CharSequence?,
        start: Int,
        end: Int,
        first: Boolean,
        layout: Layout?
    ) {
        val paint = p ?: return
        if (first) {
            val style = paint.style
            val oldColor = paint.color
            paint.color = context.getColorFromAttr(R.attr.titleTextColor)
            paint.style = Paint.Style.FILL
            this.paint = paint
            when (this.style) {
                BULLET -> {
                    val xPosition: Float = leading.toFloat() + x + dir * bulletRadius.toFloat()
                    val yPosition = (top + bottom) / 2f

                    c?.drawCircle(xPosition, yPosition, bulletRadius.toFloat(), p)
                }

                else -> {
                    var index = 1
                    if (text is SpannableStringBuilder) {
                        index = getIndex(text, start)
                    } else if (text is SpannedString) {
                        index = getIndex(text, start)
                    }
                    val xPosition: Float = leading.toFloat()
                    c?.drawText("$index.", xPosition, bottom - p.descent(), p)
                }
            }
            paint.color = oldColor
            paint.style = style
        }
    }

    private fun getIndex(span: SpannableStringBuilder, start: Int): Int {
        val startNewLine = span.getBeginNewLineLocation(start)
        val bulletSpans = span.getSpans(0, start, CustomLeadingMarginSpan::class.java)
            .filter { it.style == style }

        var indexes = span.toString().indexesOf("\n")

        indexes = indexes.filter { it <= startNewLine }.filter {
            bulletSpans.any { bullet ->
                val startSpan = span.getSpanStart(bullet)
                val endSpan = span.getSpanEnd(bullet)

                it in startSpan + 1..endSpan
            }
        }

        return indexes.size + 1
    }


    private fun getIndex(span: SpannedString, start: Int): Int {
        val startNewLine = span.getBeginNewLineLocation(start)
        val bulletSpans = span.getSpans(0, start, CustomLeadingMarginSpan::class.java)
            .filter { it.style == style }

        var indexes = span.toString().indexesOf("\n")

        indexes = indexes.filter { it <= startNewLine }.filter {
            bulletSpans.any { bullet ->
                val startSpan = span.getSpanStart(bullet)
                val endSpan = span.getSpanEnd(bullet)

                it in startSpan + 1..endSpan
            }
        }

        return indexes.size + 1
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        writeToParcelInternal(dest, flags)
    }

    override fun getSpanTypeId(): Int {
        return getSpanTypeIdInternal()
    }

    fun getSpanTypeIdInternal(): Int {
        return 0
    }

    fun writeToParcelInternal(dest: Parcel, flags: Int) {
        dest.writeInt(leading)
        dest.writeInt(bulletGapWidth)
        dest.writeInt(bulletRadius)
        dest.writeInt(style)
    }
}