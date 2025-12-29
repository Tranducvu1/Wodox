package com.wodox.core.extension


import android.text.Spannable
import android.text.SpannableStringBuilder

/**
 * Extension function to add a span to SpannableStringBuilder
 */
fun <T : Any> SpannableStringBuilder.addSpan(
    span: T,
    start: Int,
    end: Int,
    flags: Int = Spannable.SPAN_INCLUSIVE_INCLUSIVE
) {
    this.setSpan(span, start, end, flags)
}

/**
 * Extension function to remove spans of a specific type from SpannableStringBuilder
 */
fun <T : Any> SpannableStringBuilder.removeSpan(
    start: Int,
    end: Int,
    spanType: Class<T>
) {
    val spans = this.getSpans(start, end, spanType)
    for (span in spans) {
        val spanStart = this.getSpanStart(span)
        val spanEnd = this.getSpanEnd(span)

        if (spanStart < start) {
            // Re-add span from beginning to start position
            this.setSpan(span, spanStart, start, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
        }

        if (spanEnd > end) {
            // Re-add span from end position to original end
            this.setSpan(span, end, spanEnd, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
        }

        // Remove original span
        this.removeSpan(span)
    }
}