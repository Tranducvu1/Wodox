package com.wodox.main.ui.main.createbottomsheet

import androidx.recyclerview.widget.LinearLayoutManager
import com.wodox.core.extension.debounceClick
import com.wodox.core.extension.screenHeight
import com.wodox.core.base.fragment.BaseBottomSheetDialogFragment
import com.wodox.main.R
import com.wodox.main.databinding.FragmentCreateBottomSheetBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreateBottomSheet :
    BaseBottomSheetDialogFragment<FragmentCreateBottomSheetBinding, CreateViewModel>(
        CreateViewModel::class
    ) {

    private val createAdapter by lazy {
        CreateAdapter(
            requireContext(),
            object : CreateAdapter.OnItemClickListener {
            }
        )
    }


    override fun layoutId() = R.layout.fragment_create_bottom_sheet

    override fun initialize() {
        setupUI()
        setupRecyclerView()
        setupActions()
    }

    private fun setupUI() {
        binding.root.layoutParams = binding.root.layoutParams.apply {
            height = (requireActivity().screenHeight * 0.95).toInt()
        }
    }

    private fun setupRecyclerView() {
        binding.rvCreateOptions.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = createAdapter
        }
    }

    private fun setupActions() {
        binding.ivClose.debounceClick {
            dismissAllowingStateLoss()
        }
    }
}