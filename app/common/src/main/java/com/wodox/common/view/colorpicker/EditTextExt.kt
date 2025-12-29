package com.wodox.common.view.colorpicker

import android.graphics.Color
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.SpannedString
import android.text.style.AbsoluteSizeSpan
import android.text.style.BackgroundColorSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StrikethroughSpan
import android.widget.EditText
import com.onegravity.rteditor.spans.BoldSpan
import com.onegravity.rteditor.spans.ItalicSpan
import com.onegravity.rteditor.spans.UnderlineSpan
import com.wodox.core.extension.addSpan
import com.wodox.core.extension.removeSpan
import com.wodox.core.extension.color
import com.wodox.core.extension.indexesOf
import com.wodox.core.extension.px
import com.wodox.domain.docs.model.TextFormat
import kotlin.math.max

fun EditText.applyFont(fontItem: TextFormat) {
    val span = text as SpannableStringBuilder
    val begin = selectionStart
    var end = selectionEnd
    if (selectionStart == selectionEnd && selectionStart == span.length) {
        end = span.length
    }

    if (begin < 0 || end < 0 || begin > end) {
        return
    }

    when (fontItem.componentToChange) {
        TextFormat.ChangeComponent.TEXT_COLOR -> {
            span.removeSpan(begin, end, ForegroundColorSpan::class.java)
            span.addSpan(
                ForegroundColorSpan(fontItem.rawColor?.color ?: Color.BLACK),
                begin,
                end,
                Spannable.SPAN_INCLUSIVE_INCLUSIVE
            )
        }

        TextFormat.ChangeComponent.TEXT_SIZE -> {
            span.removeSpan(begin, end, AbsoluteSizeSpan::class.java)

            span.addSpan(
                AbsoluteSizeSpan(fontItem.textSize.toInt().px, false),
                begin,
                end,
                Spannable.SPAN_INCLUSIVE_INCLUSIVE
            )
            text = span
            setSelection(begin, end)
        }

        TextFormat.ChangeComponent.PAINT_FLAG -> {
            if (fontItem.isUnderline) {
                span.addSpan(
                    UnderlineSpan(),
                    begin,
                    end,
                    Spannable.SPAN_INCLUSIVE_INCLUSIVE
                )
            } else {
                span.removeSpan(
                    begin,
                    end,
                    UnderlineSpan::class.java
                )
            }
            if (fontItem.isStrikeThough) {
                span.addSpan(
                    StrikethroughSpan(),
                    begin,
                    end,
                    Spannable.SPAN_INCLUSIVE_INCLUSIVE
                )
            } else {
                span.removeSpan(
                    begin,
                    end,
                    StrikethroughSpan::class.java
                )
            }
        }

        TextFormat.ChangeComponent.HIGHLIGHT_COLOR -> {
            span.removeSpan(
                begin,
                end, BackgroundColorSpan::class.java
            )
            if (fontItem.highLightColor != null) {
                span.addSpan(
                    BackgroundColorSpan(fontItem.highLightColor ?: Color.TRANSPARENT),
                    begin,
                    end,
                    Spannable.SPAN_INCLUSIVE_INCLUSIVE
                )
            }
        }

        TextFormat.ChangeComponent.FONT -> {
            var style = fontItem.getFontStyle()

            val typefaceSpan = CustomTypefaceSpan(
                fontName = fontItem.fontName,
                context = context,
                style = style
            )

            span.removeSpan(
                begin,
                end, android.text.style.TypefaceSpan::class.java
            )
            span.addSpan(
                typefaceSpan,
                begin,
                end,
                Spannable.SPAN_INCLUSIVE_INCLUSIVE
            )
        }

        TextFormat.ChangeComponent.FONT_STYLE -> {
            var style = fontItem.getFontStyle()

            span.removeSpan(
                begin,
                end, BoldSpan::class.java
            )
            span.removeSpan(
                begin,
                end, ItalicSpan::class.java
            )

            when (style) {
                Typeface.BOLD -> {
                    span.addSpan(
                        BoldSpan(),
                        begin,
                        end,
                        Spannable.SPAN_INCLUSIVE_INCLUSIVE
                    )
                }

                Typeface.ITALIC -> {
                    span.addSpan(
                        ItalicSpan(),
                        begin,
                        end,
                        Spannable.SPAN_INCLUSIVE_INCLUSIVE
                    )
                }

                Typeface.BOLD_ITALIC -> {
                    span.addSpan(
                        BoldSpan(),
                        begin,
                        end,
                        Spannable.SPAN_INCLUSIVE_INCLUSIVE
                    )
                    span.addSpan(
                        ItalicSpan(),
                        begin,
                        end,
                        Spannable.SPAN_INCLUSIVE_INCLUSIVE
                    )
                }

                else -> {

                }
            }


        }

        TextFormat.ChangeComponent.BULLET_NUMERIC -> {
            addBullet(CustomLeadingMarginSpan.NUMERIC)
        }

        TextFormat.ChangeComponent.BULLET -> {
            addBullet(CustomLeadingMarginSpan.BULLET)
        }

        else -> {
        }
    }
}

fun EditText.addBullet(style: Int = CustomLeadingMarginSpan.BULLET) {
    val span = text as SpannableStringBuilder
    val begin = selectionStart
    val newLinePosition = span.getBeginNewLineLocation(begin)
    val nextNewLinePosition = span.getNextNewLineLocation(begin)
    val bulletSpans = span.getBulletInPosition(begin)
    if (bulletSpans.isNotEmpty()) {
        for (bulletSpan in bulletSpans) {
            val bulletStart = span.getSpanStart(bulletSpan)
            val bulletEnd = span.getSpanEnd(bulletSpan)
            span.removeSpan(bulletSpan)
            if (bulletStart < newLinePosition) {
                span.addSpan(
                    CustomLeadingMarginSpan(
                        context,
                        bulletSpan.leading,
                        bulletSpan.bulletGapWidth,
                        bulletSpan.bulletRadius,
                        bulletSpan.style
                    ), bulletStart, newLinePosition, Spannable.SPAN_INCLUSIVE_INCLUSIVE
                )
            }
            if (nextNewLinePosition > -1 && nextNewLinePosition < bulletEnd) {
                span.addSpan(
                    CustomLeadingMarginSpan(
                        context,
                        bulletSpan.leading,
                        bulletSpan.bulletGapWidth,
                        bulletSpan.bulletRadius,
                        bulletSpan.style
                    ), nextNewLinePosition + 1, bulletEnd, Spannable.SPAN_INCLUSIVE_INCLUSIVE
                )
            }
        }
    } else {
        span.addSpan(
            CustomLeadingMarginSpan(
                context,
                32, if (style == CustomLeadingMarginSpan.BULLET) {
                    32
                } else {
                    8
                }, bulletRadius = 8, style = style
            ),
            newLinePosition,
            begin,
            Spannable.SPAN_INCLUSIVE_INCLUSIVE
        )
    }
}

fun SpannableStringBuilder.getBulletInPosition(position: Int): ArrayList<CustomLeadingMarginSpan> {
    val spans = ArrayList<CustomLeadingMarginSpan>()

    val bulletSpan = this.getSpans(
        0, position,
        CustomLeadingMarginSpan::class.java
    )

    for (span in bulletSpan) {
        val start = getSpanStart(span)
        val end = getSpanEnd(span)

        if (position in start..end) {
            spans.add(span)
        }
    }

    return spans
}

fun SpannableStringBuilder.getBeginNewLineLocation(end: Int): Int {
    val index = this.toString().substring(0, end).lastIndexOf("\n")

    return max(0, index)
}

fun SpannedString.getBeginNewLineLocation(end: Int): Int {
    val index = this.toString().substring(0, end).lastIndexOf("\n")

    return max(0, index)
}

fun SpannableStringBuilder.getNextNewLineLocation(end: Int): Int {
    val indexes = this.toString().indexesOf("\n")

    return indexes.firstOrNull { it >= end } ?: -1
}

