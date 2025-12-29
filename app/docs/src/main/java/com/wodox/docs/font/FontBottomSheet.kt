package com.wodox.docs.font

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.wodox.docs.R
import com.wodox.common.navigation.reloadChangedItems
import com.wodox.core.base.fragment.BaseBottomSheetDialogFragment
import com.wodox.core.extension.addSpaceDecoration
import com.wodox.core.extension.dialogWidth
import com.wodox.core.extension.screenHeight
import com.wodox.core.extension.screenWidth
import dagger.hilt.android.AndroidEntryPoint
import com.wodox.docs.databinding.FragmentFontBottomSheetBinding
import com.wodox.docs.model.Constants
import com.wodox.domain.docs.model.TextFormat

@AndroidEntryPoint
class FontBottomSheet :
    BaseBottomSheetDialogFragment<FragmentFontBottomSheetBinding, FontViewModel>(
        FontViewModel::class
    ) {

    override fun layoutId(): Int = R.layout.fragment_font_bottom_sheet

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
        binding.lifecycleOwner = this
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
            if (viewModel.isFirstTime) {
                viewModel.isFirstTime = false
                return@observe
            }
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
        fun newInstance(textFormat: TextFormat) = FontBottomSheet().apply {
            arguments = Bundle().apply {
                putParcelable(Constants.Intents.TEXT_FORMAT, textFormat)
            }
        }
    }
}