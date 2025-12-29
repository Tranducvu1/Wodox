package com.wodox.docs.extention

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import jp.wasabeef.richeditor.RichEditor

class MyRichEditor(context: Context, attrs: AttributeSet? = null) : RichEditor(context, attrs) {

    fun setFontName(fontName: String, applyToAll: Boolean = true) {
        try {
            exec("javascript:RE.prepareInsert();")

            if (applyToAll) {
                val js = """
                    javascript:(function() {
                        document.execCommand('selectAll', false, null);
                        document.execCommand('fontName', false, '${fontName}');
                        document.getSelection().removeAllRanges();
                    })();
                """.trimIndent()
                exec(js)
            } else {
                val js = "javascript:RE.setFont('${fontName}');"
                exec(js)
            }

        } catch (e: Exception) {
            Log.e("MyRichEditor", "Error setting font name: ${e.message}")
        }
    }
}
