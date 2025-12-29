package com.wodox.common.view.colorpicker

import android.content.Context
import android.graphics.Typeface
import android.text.Editable
import android.text.TextWatcher
import android.text.style.AbsoluteSizeSpan
import android.text.style.BackgroundColorSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StrikethroughSpan
import android.text.style.StyleSpan
import android.text.style.UnderlineSpan
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import com.onegravity.rteditor.spans.BoldSpan
import com.onegravity.rteditor.spans.ItalicSpan
import com.wodox.core.extension.dp
import com.wodox.core.extension.rawColor
import com.wodox.core.extension.sp
import com.wodox.domain.docs.model.TextFormat
import kotlin.math.round

class AppEditText(context: Context, attrs: AttributeSet?, defStyle: Int) :
    AppCompatEditText(context, attrs, defStyle), TextWatcher {
    constructor(context: Context, attrs: AttributeSet) : this(context, attrs, 0)

    var oldText: String? = null

    interface OnAppEditTextListener {
        fun onAfterTextChanged(v: AppEditText, s: Editable?)

        fun onSelectionChange(v: AppEditText, start: Int, end: Int)

        fun onFocusChange(hasFocus: Boolean)

        fun onFormatChanged() {}
    }

    init {
        isFocusableInTouchMode = true
        isFocusable = true

        setOnFocusChangeListener { v, hasFocus ->
            textListener?.onFocusChange(hasFocus)
        }

        addTextChangedListener(this)

//        movementMethod = LinkMovementMethod()

    }

    var textListener: OnAppEditTextListener? = null


    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        oldText = s?.toString().orEmpty()
    }

    override fun afterTextChanged(s: Editable?) {
        textListener?.onAfterTextChanged(this, s)
    }

    override fun onTextChanged(
        text: CharSequence?,
        start: Int,
        lengthBefore: Int,
        lengthAfter: Int
    ) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter)
    }

    override fun onSelectionChanged(start: Int, end: Int) {
        textListener?.onSelectionChange(this, start, end)
        super.onSelectionChanged(start, end)
    }
}

fun AppEditText.applyTextFormat(font: TextFormat, oldFont: TextFormat) {
    applyFont(font)
    textListener?.onFormatChanged()
}

fun AppEditText.getCurrentTextFormat(): TextFormat {
    val textFormat = TextFormat()

    val text = text ?: return textFormat

    val spans = text.getSpans(0, text.length, Any::class.java)
    val currentCursor = selectionStart

    for (span in spans) {
        val start = text.getSpanStart(span)
        val end = text.getSpanEnd(span)

        if (currentCursor in start..end) {

            when (span) {
                is UnderlineSpan -> {
                    textFormat.fontStyles.add(TextFormat.FontStyle.UNDERLINE)
                }

                is ItalicSpan -> {
                    textFormat.fontStyles.add(TextFormat.FontStyle.ITALIC)
                }

                is BoldSpan -> {
                    textFormat.fontStyles.add(TextFormat.FontStyle.BOLD)
                }

                is StrikethroughSpan -> {
                    textFormat.fontStyles.add(TextFormat.FontStyle.STRIKE_THROUGH)
                }

                is ForegroundColorSpan -> {
                    textFormat.rawColor = span.foregroundColor.rawColor()
                }

                is AbsoluteSizeSpan -> {
                    1.dp
                    textFormat.textSize = round(span.size.toFloat().sp)
                }

                is BackgroundColorSpan -> {
                    textFormat.rawHighLightColor = span.backgroundColor.rawColor()
                }

                is CustomLeadingMarginSpan -> {
                    if (span.style == CustomLeadingMarginSpan.BULLET) {
                        textFormat.bulletType = TextFormat.BulletType.BULLET
                    } else if (span.style == CustomLeadingMarginSpan.NUMERIC) {
                        textFormat.bulletType = TextFormat.BulletType.BULLET_NUMERIC
                    }
                }

                is CustomTypefaceSpan -> {
                    textFormat.fontName = span.fontName
                    val styles = when (span.style) {
                        Typeface.BOLD_ITALIC ->
                            arrayListOf(TextFormat.FontStyle.BOLD, TextFormat.FontStyle.ITALIC)

                        Typeface.BOLD -> arrayListOf(TextFormat.FontStyle.BOLD)

                        Typeface.ITALIC -> arrayListOf(TextFormat.FontStyle.ITALIC)

                        else -> arrayListOf()
                    }

                    textFormat.fontStyles.addAll(styles)
                }

                is StyleSpan -> {
                    val styles = when (span.style) {
                        Typeface.BOLD_ITALIC ->
                            arrayListOf(TextFormat.FontStyle.BOLD, TextFormat.FontStyle.ITALIC)

                        Typeface.BOLD -> arrayListOf(TextFormat.FontStyle.BOLD)

                        Typeface.ITALIC -> arrayListOf(TextFormat.FontStyle.ITALIC)

                        else -> arrayListOf()
                    }

                    textFormat.fontStyles.addAll(styles)
                }
            }
        }
    }

    return textFormat
}