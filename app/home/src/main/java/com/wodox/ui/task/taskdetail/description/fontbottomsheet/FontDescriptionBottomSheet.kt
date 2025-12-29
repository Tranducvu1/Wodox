package com.wodox.ui.task.taskdetail.description.fontbottomsheet

import android.app.Dialog
import android.os.Bundle
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.wodox.common.navigation.reloadChangedItems
import com.wodox.core.base.fragment.BaseBottomSheetDialogFragment
import com.wodox.core.extension.addSpaceDecoration
import com.wodox.core.extension.dialogWidth
import com.wodox.core.extension.screenHeight
import com.wodox.core.extension.screenWidth
import dagger.hilt.android.AndroidEntryPoint
import com.wodox.domain.docs.model.TextFormat
import com.wodox.home.R
import com.wodox.home.databinding.FragmentFontDescriptionBottomSheetBinding
import com.wodox.model.Constants

@AndroidEntryPoint
class FontDescriptionBottomSheet :
    BaseBottomSheetDialogFragment<FragmentFontDescriptionBottomSheetBinding, FontDescriptionViewModel>(
        FontDescriptionViewModel::class) {

    override fun layoutId(): Int = R.layout.fragment_font_description_bottom_sheet

    interface OnItemClickListener {
        fun onClick(font: TextFormat)
    }

    var listener: OnItemClickListener? = null

    override fun initialize() {
        setSize(
            requireActivity().dialogWidth,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        observe()
        setupUI()
        setupActions()
        setupRecyclerView()
    }

    private fun setupUI() {
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            root.layoutParams = root.layoutParams.apply {
                height = requireActivity().screenHeight * 9 / 10
            }
        }
    }

    private fun setupActions() {
        binding.apply {
            ivClose.setOnClickListener {
                dismissAllowingStateLoss()
            }
        }
    }

    private fun observe() {
        viewModel.selectedFont.observe(viewLifecycleOwner) { font ->
            font?.let {
                handleFontSelectionChange(it)
            }
        }
    }

    private fun handleFontSelectionChange(font: TextFormat) {
        listener?.onClick(font)
        dismissAllowingStateLoss()
    }

    private fun setupRecyclerView() {
        binding.apply {
            val spacing = requireContext().resources.getDimension(com.wodox.core.R.dimen.dp_8)
            val itemWidth = requireActivity().screenWidth / 3 - 2 * spacing

            recyclerView.apply {
                layoutManager = createGridLayoutManager(itemWidth)
                addSpaceDecoration(spacing.toInt(), true)
                adapter = createFontAdapter()
            }
        }
    }

    private fun createGridLayoutManager(itemWidth: Float): GridLayoutManager {
        return object : GridLayoutManager(context, 3, VERTICAL, false) {
            override fun checkLayoutParams(lp: RecyclerView.LayoutParams?): Boolean {
                lp?.width = itemWidth.toInt()
                return true
            }
        }
    }

    private fun createFontAdapter(): FontAdapter {
        return FontAdapter(
            requireContext(),
            object : FontAdapter.OnClickListener {
                override fun onClick(font: TextFormat) {
                    handleFontSelection(font)
                }
            })
    }

    private fun handleFontSelection(font: TextFormat) {
        selectFont(font)
    }

    private fun selectFont(font: TextFormat) {
        viewModel.selectedFont.value = viewModel.selectedFont.value?.apply {
            componentToChange = TextFormat.ChangeComponent.FONT
            fontName = font.fontName
        }

        binding.recyclerView.reloadChangedItems<TextFormat>(predicate = {
            it.fontName == font.fontName
        })
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        dialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
        return dialog
    }

    companion object {
        @JvmStatic
        fun newInstance(textFormat: TextFormat) = FontDescriptionBottomSheet().apply {
            arguments = Bundle().apply {
                putParcelable(Constants.Intents.TEXT_FORMAT, textFormat)
            }
        }
    }
}