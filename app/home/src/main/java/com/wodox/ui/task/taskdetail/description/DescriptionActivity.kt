package com.wodox.ui.task.taskdetail.description

import android.graphics.Color
import android.graphics.Rect
import android.text.Editable
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.lifecycleScope
import com.wodox.common.extension.showMoreColor
import com.wodox.common.util.KeyboardUtil
import com.wodox.common.view.colorpicker.AppEditText
import com.wodox.common.view.colorpicker.TextSizePopupView
import com.wodox.common.view.colorpicker.applyTextFormat
import com.wodox.domain.docs.model.TextFormat
import com.wodox.domain.docs.model.TextFormat.ChangeComponent
import com.wodox.domain.docs.model.TextFormat.FontStyle
import com.wodox.home.R
import com.wodox.home.databinding.ActivityDescriptionLayoutBinding
import com.wodox.ui.task.taskdetail.description.fontbottomsheet.FontDescriptionBottomSheet
import com.wodox.ui.task.taskdetail.description.optionmenu.DescriptionOptionMenu
import com.wodox.ui.task.taskdetail.description.optionmenu.DescriptionOptionMenu.TextFormatMenuType
import com.wodox.ui.task.taskdetail.description.optionmenu.DescriptionOptionMenuView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import com.wodox.common.util.HtmlConverterUtil
import kotlinx.coroutines.Dispatchers
import android.text.TextWatcher
import com.wodox.core.base.activity.BaseActivity
import com.wodox.core.extension.debounceClick
import com.wodox.core.extension.keyboardVisibilityChanges
import com.wodox.core.extension.launchWhenStarted
import com.wodox.core.extension.show
import com.wodox.core.extension.gone
import com.wodox.core.extension.px
import com.wodox.core.extension.showAllowingStateLoss
import com.wodox.core.extension.toast
import com.wodox.core.util.hideKeyboard


@AndroidEntryPoint
class DescriptionActivity :
    BaseActivity<ActivityDescriptionLayoutBinding, DescriptionUiViewModel>(DescriptionUiViewModel::class) {
    private var isEditing = false

    override fun layoutId() = R.layout.activity_description_layout

    override fun initialize() {
        setupFocusListener()
        setupKeyboardVisibilityListener()
        KeyboardUtil.listenerKeyboardVisibleForAndroid15AndAbove(this, binding.container)
        setupUI()
        setupAction()
        observer()
    }

    private fun setupUI() {
        val html = viewModel.uiState.value.task?.description.orEmpty()
        if (html.isNotEmpty()) {
            lifecycleScope.launch(Dispatchers.IO) {
                val spanned = HtmlConverterUtil.convertToSpanned(html)
                launch(Dispatchers.Main) {
                    binding.etDescription.setText(spanned, TextView.BufferType.SPANNABLE)
                }
            }
        }
    }

    private fun setupAction() {
        setupOptionMenu()
        binding.fabEdit.debounceClick {
            if (!isEditing) {
                enableEditing()
            }
        }

        binding.btnDone.debounceClick {
            if (isEditing) {
                disableEditing()
            } else {
                finish()
            }
        }

        binding.btnDone.debounceClick {
            val editableContent = binding.etDescription.text
            val htmlContent = HtmlConverterUtil.convertToHtml(editableContent)
            viewModel.dispatch(DescriptionUiAction.SaveDescription(htmlContent))
        }

        binding.etDescription.debounceClick {
            if (isEditing) {
                enableEditing()
            }
        }
        binding.etDescription.addTextChangedListener(
            object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    countLengthText(s, binding.tvCharCount)
                }
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            }
        )
        binding.ivBack.debounceClick {
            finish()
        }
    }

    private fun observer() {
        launchWhenStarted {
            viewModel.uiEvent.collect { event ->
                when (event) {
                    DescriptionUiEvent.SaveSuccess -> {
                        toast("Save description successfully")
                        finish()
                    }
                }
            }
        }
    }

    private fun disableEditing() {
        isEditing = false
        binding.etDescription.apply {
            isEnabled = false
            isFocusable = false
            isFocusableInTouchMode = false
            isCursorVisible = false
            clearFocus()
        }
        viewModel.isFormatFont.set(false)
        hideKeyboard()
    }


    private fun enableEditing() {
        setupOptionMenu()
        isEditing = true
        binding.etDescription.apply {
            isEnabled = true
            isFocusable = true
            isFocusableInTouchMode = true
            isCursorVisible = true
            requestFocus()
        }
        viewModel.isFormatFont.set(true)
        KeyboardUtil.listenerKeyboardVisibleForAndroid15AndAbove(this, binding.container)
        viewModel.isFormatFont.set(true)
    }

    override fun onResume() {
        super.onResume()
        setupOptionMenu()
    }

    private fun setupFocusListener() {
        binding.etDescription.setOnFocusChangeListener { _, hasFocus ->
            viewModel.isFormatFont.set(hasFocus)
        }
    }

    private fun setupOptionMenu() {
        binding.optionMenuView.listener = object : DescriptionOptionMenuView.OnItemClickListener {
            override fun onClick(
                menu: DescriptionOptionMenu,
                view: View?
            ) {
                handleMenuClick(menu)
            }
        }
    }

    private fun handleMenuClick(menu: DescriptionOptionMenu) {
        when (menu.type) {
            TextFormatMenuType.FONT_FAMILY -> handleChangeFont()
            TextFormatMenuType.COLOR -> showTextColorPicker()
            TextFormatMenuType.SIZE -> handleTextSize()
            TextFormatMenuType.BOLD -> setTextFormat(menu.type)
            TextFormatMenuType.ITALIC -> setTextFormat(menu.type)
            TextFormatMenuType.STRIKE_THROUGH -> setTextFormat(menu.type)
            TextFormatMenuType.UNDERLINE -> setTextFormat(menu.type)
            TextFormatMenuType.HIGHLIGHT -> showHighlightColorPicker()
        }
    }

    private fun showHighlightColorPicker() {
        binding.tvTextSize.gone()
        setupColorPicker { selectedColor ->
            viewModel.textFormat.rawHighLightColor = String.format("#%08X", selectedColor)
            viewModel.textFormat.componentToChange = ChangeComponent.HIGHLIGHT_COLOR
            binding.optionMenuView.setColorHighLight(selectedColor)
            setupFlashCardFormat()
        }
    }

    private fun showTextColorPicker() {
        binding.tvTextSize.gone()
        setupColorPicker { selectedColor ->
            viewModel.textFormat.rawColor = String.format("#%08X", selectedColor)
            viewModel.textFormat.componentToChange = ChangeComponent.TEXT_COLOR
            binding.optionMenuView.setColorFormat(selectedColor)
            setupFlashCardFormat()
        }
    }


    private fun setTextFormat(type: TextFormatMenuType) {
        val fontStyle = when (type) {
            TextFormatMenuType.BOLD -> FontStyle.BOLD
            TextFormatMenuType.ITALIC -> FontStyle.ITALIC
            TextFormatMenuType.UNDERLINE -> FontStyle.UNDERLINE
            TextFormatMenuType.STRIKE_THROUGH -> FontStyle.STRIKE_THROUGH
            else -> null
        }

        fontStyle?.let {
            if (!viewModel.textFormat.fontStyles.contains(it)) {
                viewModel.textFormat.fontStyles.add(it)
            } else {
                viewModel.textFormat.fontStyles.remove(it)
            }
            viewModel.textFormat.componentToChange = when (type) {
                TextFormatMenuType.UNDERLINE, TextFormatMenuType.STRIKE_THROUGH -> ChangeComponent.PAINT_FLAG
                TextFormatMenuType.BOLD, TextFormatMenuType.ITALIC -> TextFormat.ChangeComponent.FONT_STYLE
                else -> null
            }
            setupFlashCardFormat()
        }
    }

    private fun handleTextSize() {
        showTextSizePopupBelowItem(1)
        binding.tvTextSize.size = viewModel.textFormat.textSize.toInt()
        binding.tvTextSize.listener = object : TextSizePopupView.OnItemClickListener {
            override fun onScrollStop(size: Int) {
                viewModel.textFormat.textSize = size.toFloat()
                viewModel.textFormat.componentToChange = TextFormat.ChangeComponent.TEXT_SIZE
                binding.optionMenuView.setTextSize(size)
                setupFlashCardFormat()
                binding.tvTextSize.gone()
            }
        }
    }

    private fun setupKeyboardVisibilityListener() {
        lifecycleScope.launch(Dispatchers.Main) {
            binding.container.keyboardVisibilityChanges().collect { isVisible ->
                viewModel.visibleKeyboard.set(isVisible)
                binding.fabEdit.show(!isVisible)
                if (!isVisible) {
                    viewModel.isFormatFont.set(false)
                    return@collect
                }
                val edit = currentFocus
                if (edit is AppEditText) {
                    binding.optionMenuView.post {
                        scrollCursorPosition(
                            edit,
                            binding.nestedScrollView,
                            binding.optionMenuView.height
                        )
                    }
                }
            }
        }
    }

    private fun setupFlashCardFormat() {
        val appEditText = currentFocus as? AppEditText
        appEditText?.applyTextFormat(viewModel.textFormat, viewModel.textFormat)
    }

    private fun setupColorPicker(onColorSelected: (Int) -> Unit) {
        this@DescriptionActivity.showMoreColor(
            defaultColor = ContextCompat.getColor(
                this@DescriptionActivity, com.wodox.core.R.color.black
            ),
            success = { selectedColor ->
                onColorSelected(selectedColor)
            },
            cancel = { onColorSelected(Color.BLACK) }
        )
    }

    fun showTextSizePopupBelowItem(position: Int) {
        val vh = binding.optionMenuView.viewBinding().recyclerView.findViewHolderForAdapterPosition(
            position
        )
            ?: return
        val itemView = vh.itemView

        itemView.post {
            val itemLocation = IntArray(2)
            itemView.getLocationOnScreen(itemLocation)

            val rootLocation = IntArray(2)
            binding.root.getLocationOnScreen(rootLocation)

            val relativeX = (itemLocation[0] - rootLocation[0] + 32.px / 2).toFloat()

            binding.tvTextSize.x = relativeX
            binding.tvTextSize.size = viewModel.textFormat.textSize.toInt()
            binding.tvTextSize.show()
        }
    }

    private fun handleChangeFont() {
        val bottomSheet = FontDescriptionBottomSheet.newInstance(viewModel.textFormat)
        bottomSheet.listener = object : FontDescriptionBottomSheet.OnItemClickListener {
            override fun onClick(font: TextFormat) {
                viewModel.textFormat.fontName = font.fontName
                viewModel.textFormat.componentToChange = TextFormat.ChangeComponent.FONT
                setupFlashCardFormat()
            }
        }
        bottomSheet.showAllowingStateLoss(supportFragmentManager,"")
    }


    private fun scrollCursorPosition(
        editText: AppEditText,
        scrollView: NestedScrollView,
        extraBottom: Int
    ) {
        editText.post {
            val layout = editText.layout ?: return@post
            val line = layout.getLineForOffset(editText.selectionStart)
            val cursorY = layout.getLineBottom(line)

            val location = IntArray(2)
            editText.getLocationOnScreen(location)

            val cursorScreenY = location[1] + cursorY
            val rect = Rect()
            scrollView.getWindowVisibleDisplayFrame(rect)

            val visibleBottom = rect.bottom - extraBottom

            if (cursorScreenY > visibleBottom) {
                scrollView.smoothScrollBy(0, cursorScreenY - visibleBottom)
            }
        }
    }

    private fun countLengthText(
        text: Editable?,
        tv: TextView,
    ) {
        val length = text?.length ?: 0
        tv.show(length > 0)
        tv.text = applicationContext.getString(
            com.wodox.resources.R.string.char_count, length, 200
        )
    }

}
