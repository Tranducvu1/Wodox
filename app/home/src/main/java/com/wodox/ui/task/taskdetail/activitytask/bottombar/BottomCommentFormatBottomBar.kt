package com.wodox.ui.task.taskdetail.activitytask.bottombar

import android.content.Context
import android.text.Spanned
import android.text.style.StrikethroughSpan
import android.text.style.StyleSpan
import android.text.style.UnderlineSpan
import android.util.AttributeSet
import android.view.View
import android.widget.EditText
import android.widget.HorizontalScrollView
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.wodox.home.databinding.BottomCommentFormatBarBinding
import android.graphics.Typeface
import android.text.Editable

class BottomCommentFormatBottomBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private lateinit var binding: BottomCommentFormatBarBinding

    private lateinit var edtComment: EditText
    private lateinit var llFormatOptions: HorizontalScrollView
    private lateinit var btnToggleFormat: ImageView
    private lateinit var btnBold: ImageView
    private lateinit var btnItalic: ImageView
    private lateinit var btnUnderline: ImageView
    private lateinit var btnStrikethrough: ImageView
    private lateinit var btnCode: ImageView
    private lateinit var btnLink: ImageView
    private lateinit var btnSend: ImageView
    private lateinit var btnCancel: TextView
    private lateinit var editModeIndicator: ConstraintLayout

    private var onSendListener: ((String) -> Unit)? = null
    private var onCancelListener: (() -> Unit)? = null
    private var isFormatVisible = false
    private var isEditMode = false

    init {
        initView()
        setupListeners()
    }

    private fun initView() {
        binding = BottomCommentFormatBarBinding.inflate(
            android.view.LayoutInflater.from(context),
            this,
            true
        )

        edtComment = binding.edtComment
        llFormatOptions = binding.llFormatOptions
        btnToggleFormat = binding.btnToggleFormat
        btnBold = binding.btnBold
        btnItalic = binding.btnItalic
        btnUnderline = binding.btnUnderline
        btnStrikethrough = binding.btnStrikethrough
        btnCode = binding.btnCode
        btnLink = binding.btnLink
        btnSend = binding.btnSend
        btnCancel = binding.btnCancel
        editModeIndicator = binding.editModeIndicator
    }

    private fun setupListeners() {
        btnToggleFormat.setOnClickListener {
            toggleFormatOptions()
        }

        btnBold.setOnClickListener {
            applyBoldFormat()
        }

        btnItalic.setOnClickListener {
            applyItalicFormat()
        }

        btnUnderline.setOnClickListener {
            applyUnderlineFormat()
        }

        btnStrikethrough.setOnClickListener {
            applyStrikethroughFormat()
        }

        btnCode.setOnClickListener {
            applyCodeFormat()
        }

        btnLink.setOnClickListener {
            applyLinkFormat()
        }

        btnSend.setOnClickListener {
            sendComment()
        }

        btnCancel.setOnClickListener {
            onCancelListener?.invoke()
        }
    }

    private fun toggleFormatOptions() {
        isFormatVisible = !isFormatVisible
        llFormatOptions.visibility = if (isFormatVisible) VISIBLE else GONE
        btnToggleFormat.alpha = if (isFormatVisible) 1f else 0.6f
    }

    private fun applyBoldFormat() {
        val start = edtComment.selectionStart
        val end = edtComment.selectionEnd

        if (start != end) {
            val text = edtComment.text
            val spannable = text as? Editable ?: return

            spannable.setSpan(
                StyleSpan(Typeface.BOLD),
                start,
                end,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        } else {
            edtComment.append("**")
            edtComment.setSelection(edtComment.text.length - 2)
        }
    }

    private fun applyItalicFormat() {
        val start = edtComment.selectionStart
        val end = edtComment.selectionEnd

        if (start != end) {
            val text = edtComment.text
            val spannable = text as? Editable ?: return

            spannable.setSpan(
                StyleSpan(Typeface.ITALIC),
                start,
                end,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        } else {
            edtComment.append("*")
            edtComment.setSelection(edtComment.text.length - 1)
        }
    }

    private fun applyUnderlineFormat() {
        val start = edtComment.selectionStart
        val end = edtComment.selectionEnd

        if (start != end) {
            val text = edtComment.text
            val spannable = text as? Editable ?: return

            spannable.setSpan(
                UnderlineSpan(),
                start,
                end,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        } else {
            edtComment.append("<u></u>")
            edtComment.setSelection(edtComment.text.length - 4)
        }
    }

    private fun applyStrikethroughFormat() {
        val start = edtComment.selectionStart
        val end = edtComment.selectionEnd

        if (start != end) {
            val text = edtComment.text
            val spannable = text as? Editable ?: return

            spannable.setSpan(
                StrikethroughSpan(),
                start,
                end,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        } else {
            edtComment.append("~~")
            edtComment.setSelection(edtComment.text.length - 2)
        }
    }

    private fun applyCodeFormat() {
        val start = edtComment.selectionStart
        val end = edtComment.selectionEnd

        if (start != end) {
            val selectedText = edtComment.text.substring(start, end)
            edtComment.text.replace(start, end, "`$selectedText`")
        } else {
            edtComment.append("``")
            edtComment.setSelection(edtComment.text.length - 1)
        }
    }

    private fun applyLinkFormat() {
        edtComment.append("[Link](url)")
    }

    private fun sendComment() {
        val comment = edtComment.text.toString().trim()

        if (comment.isNotEmpty()) {
            onSendListener?.invoke(comment)
            if (!isEditMode) {
                clearComment()
                hideFormatOptions()
            }
        }
    }

    fun setOnSendListener(listener: (String) -> Unit) {
        this.onSendListener = listener
    }

    fun setOnCancelListener(listener: () -> Unit) {
        this.onCancelListener = listener
    }

    fun getCommentText(): String = edtComment.text.toString()

    fun setCommentText(text: String) {
        edtComment.setText(text)
        edtComment.setSelection(text.length) // Move cursor to end
    }

    fun clearComment() {
        edtComment.text.clear()
    }

    fun hideFormatOptions() {
        isFormatVisible = false
        llFormatOptions.visibility = GONE
        btnToggleFormat.alpha = 0.6f
    }

    fun showFormatOptions() {
        isFormatVisible = true
        llFormatOptions.visibility = VISIBLE
        btnToggleFormat.alpha = 1f
    }

    fun setEditMode(isEditing: Boolean) {
        isEditMode = isEditing

        if (isEditing) {
            btnCancel.visibility = VISIBLE
            editModeIndicator.visibility = VISIBLE
            edtComment.hint = "Edit your comment..."
        } else {
            btnCancel.visibility = GONE
            editModeIndicator.visibility = GONE
            edtComment.hint = "Write a comment..."

        }
    }

    fun requestCommentFocus() {
        edtComment.requestFocus()
        // Show keyboard
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as? android.view.inputmethod.InputMethodManager
        imm?.showSoftInput(edtComment, android.view.inputmethod.InputMethodManager.SHOW_IMPLICIT)
    }
}