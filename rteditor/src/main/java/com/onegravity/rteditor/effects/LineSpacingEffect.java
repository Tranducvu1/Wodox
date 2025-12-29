package com.onegravity.rteditor.effects;

import android.text.Spannable;

import com.onegravity.rteditor.RTEditText;
import com.onegravity.rteditor.spans.LineSpacingSpan;
import com.onegravity.rteditor.spans.RTSpan;
import com.onegravity.rteditor.utils.Paragraph;
import com.onegravity.rteditor.utils.Selection;

import java.util.ArrayList;
import java.util.List;

public class LineSpacingEffect extends ParagraphEffect<Integer, LineSpacingSpan> {

    private ParagraphSpanProcessor<Integer> mSpans2Process = new ParagraphSpanProcessor();

    @Override
    public synchronized void applyToSelection(RTEditText editor, Selection selectedParagraphs, Integer value) {
        final Spannable str = editor.getText();

        mSpans2Process.clear();

        // Get the paragraphs within the editor
        ArrayList<Paragraph> paragraphs = editor.getParagraphs();

        // Loop through each paragraph
        for (int i = 0, size = paragraphs.size(); i < size; i++) {
            Paragraph paragraph = paragraphs.get(i);

            // Find existing LineSpacingSpan and add them to mSpans2Process to be removed
            List<RTSpan<Integer>> existingSpans = getSpans(str, paragraph, SpanCollectMode.EXACT);
            mSpans2Process.removeSpans(existingSpans, paragraph);

            // Check if the paragraph is selected, then apply the LineSpacingSpan with the specified value
            if (paragraph.isSelected(selectedParagraphs)) {
                LineSpacingSpan lineSpacingSpan = new LineSpacingSpan(value);
                mSpans2Process.addSpan(lineSpacingSpan, paragraph);
            }
        }

        // Add or remove spans
        mSpans2Process.process(str);
    }
}