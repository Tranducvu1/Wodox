package com.onegravity.rteditor.spans;

import android.graphics.Paint;
import android.text.style.LineHeightSpan;

public class LineSpacingSpan implements RTSpan<Integer>, RTParagraphSpan<Integer>, LineHeightSpan {

    private final int lineSpacingMultiplier;

    public LineSpacingSpan(int lineSpacingMultiplier) {
        this.lineSpacingMultiplier = lineSpacingMultiplier;
    }

//    @Override
//    public void chooseHeight(CharSequence text, int start, int end, int spanstartv, int v, Paint.FontMetricsInt fm) {
//        if (fm != null) {
//            int originalHeight = fm.descent - fm.ascent;
//            int newHeight = (int) (originalHeight * lineSpacingMultiplier);
//
//            int addition = newHeight - originalHeight;
//            fm.descent += addition;
//            fm.bottom += addition;
//            fm.top += addition;
//            fm.ascent += addition;
//        }
//    }

    @Override
    public RTParagraphSpan<Integer> createClone() {
        return new LineSpacingSpan(lineSpacingMultiplier);
    }

    @Override
    public Integer getValue() {
        return lineSpacingMultiplier;
    }

//    @Override
//    public void chooseHeight(CharSequence charSequence, int i, int i1, int i2, int i3, Paint.FontMetricsInt fontMetricsInt) {
//        int currentHeight = fontMetricsInt.descent - fontMetricsInt.ascent;
//
//        // Set line height
//        fontMetricsInt.descent += currentHeight;
//        fontMetricsInt.ascent -= currentHeight;
//
//        // Set line space
//        fontMetricsInt.descent += lineSpacingMultiplier;
//        fontMetricsInt.bottom += lineSpacingMultiplier;
//    }

//    @Override
//    public void chooseHeight(CharSequence charSequence, int i, int i1, int i2, int i3, Paint.FontMetricsInt fontMetricsInt) {
//        int currentHeight = fontMetricsInt.descent - fontMetricsInt.ascent;
//
//        // Set line height
//        fontMetricsInt.descent += currentHeight;
//        fontMetricsInt.ascent -= currentHeight;
//
//        // Set line space
//        fontMetricsInt.descent += lineSpacingMultiplier;
//        fontMetricsInt.bottom += lineSpacingMultiplier;
//    }


    @Override
    public void chooseHeight(CharSequence text, int start, int end, int spanstartv, int lineHeight, Paint.FontMetricsInt fontMetricsInt) {
        int currentHeight = fontMetricsInt.descent - fontMetricsInt.ascent;

        // Set line height
        fontMetricsInt.descent += currentHeight;
        fontMetricsInt.ascent -= currentHeight;

        // Set line space
        fontMetricsInt.descent += lineSpacingMultiplier;
        fontMetricsInt.bottom += lineSpacingMultiplier;
    }
}
