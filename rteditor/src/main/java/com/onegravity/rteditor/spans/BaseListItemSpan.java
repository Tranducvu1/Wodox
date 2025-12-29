package com.onegravity.rteditor.spans;

import android.text.Spanned;

abstract class BaseListItemSpan {
    float determineTextSize(Spanned spanned, int start, int end, float defaultColor) {
        // If the text size is different from default use that to determine the indicator size
        // That is determined by finding the first visible character within the list item span
        // and checking its size
        int position = firstVisibleCharIndex(spanned, start, end);
        if (position >= 0) {
            AbsoluteSizeSpan[] absoluteSizeSpans = spanned.getSpans(position, position, AbsoluteSizeSpan.class);
            if (absoluteSizeSpans.length > 0) {
                AbsoluteSizeSpan absoluteSizeSpan = absoluteSizeSpans[absoluteSizeSpans.length - 1];
                return absoluteSizeSpan.getValue();
            }
        }

        // If there are no spans or no visible characters yet use the default calculation
        return defaultColor;
    }

    private int firstVisibleCharIndex(Spanned spanned, int start, int end) {
        while (start < end) {
            if (isVisibleChar(spanned.charAt(start))) {
                return start;
            }
            start++;
        }

        return -1;
    }

    private boolean isVisibleChar(char c) {
        switch (c) {
            case '\u200B':
            case '\uFFEF':
                return false;
        }

        return true;
    }

    int determineTextColor(Spanned spanned, int start, int end, int defaultColor) {
        // If the text size is different from default use that to determine the indicator size
        // That is determined by finding the first visible character within the list item span
        // and checking its size
        int position = firstVisibleCharIndex(spanned, start, end);
        if (position >= 0) {
            ForegroundColorSpan[] foregroundColorSpans = spanned.getSpans(position, position, ForegroundColorSpan.class);
            if (foregroundColorSpans.length > 0) {
                ForegroundColorSpan foregroundColorSpan = foregroundColorSpans[foregroundColorSpans.length - 1];
                return foregroundColorSpan.getValue();
            }
        }

        // If there are no spans or no visible characters yet use the default calculation
        return defaultColor;
    }

}
