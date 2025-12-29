package com.wodox.main.ui.main.addperson

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.wodox.core.base.fragment.BaseBottomSheetDialogFragment
import com.wodox.core.extension.addSpaceDecoration
import com.wodox.core.extension.debounceClick
import com.wodox.core.extension.gone
import com.wodox.core.extension.launchWhenStarted
import com.wodox.core.extension.screenHeight
import com.wodox.core.extension.show
import com.wodox.core.extension.toast
import com.wodox.domain.user.model.User
import com.wodox.main.R
import com.wodox.main.databinding.AddPersonBottomSheetBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddPersonBottomSheet :
    BaseBottomSheetDialogFragment<AddPersonBottomSheetBinding, AddPersonUiViewModel>(AddPersonUiViewModel::class) {

    override fun layoutId(): Int = R.layout.add_person_bottom_sheet

    private val userAdapter by lazy {
        UserAdapter(
            context,
            object : UserAdapter.OnItemClickListener {
                override fun onClick(user: User) {
                    viewModel.dispatch(AddPersonUiAction.HandleMakeFriend(user.id))
                }
            }
        )
    }

    override fun initialize() {
        setUI()
        setupAction()
        observer()
        setupRecycleView()
    }

    private fun setUI() {
        binding.lifecycleOwner = this@AddPersonBottomSheet
        binding.viewModel = viewModel
        binding.root.layoutParams = binding.root.layoutParams.apply {
            height = (requireActivity().screenHeight * 0.95).toInt()
        }
    }

    private fun observer() {
        launchWhenStarted {
            viewModel.uiState.collect {
                if (it.users.isNotEmpty()) {
                    binding.rvItem.show()
                    userAdapter.submitList(it.users)
                } else {
                    binding.rvItem.gone()
                    userAdapter.submitList(emptyList())
                }
                if (!it.errorMessage.isNullOrEmpty()) {
                    requireContext().toast(it.errorMessage!!)
                }

                if (it.isLoading) {
                    binding.progressIndicator.show()
                } else {
                    binding.progressIndicator.gone()
                }
            }

        }
        launchWhenStarted {
            viewModel.uiEvent.collect { event ->
                when (event) {
                    AddPersonUiEvent.AddFriendSuccess -> requireContext().toast("Add Successfully")
                }
            }
        }
    }

    private fun setupAction() {
        binding.apply {
            tvFindUserEmail.debounceClick {
                handleFindName()
            }
            btnClose.debounceClick {
                dismissAllowingStateLoss()
            }
        }
    }

    private fun handleFindName() {
        val person = binding.etPersonName.text.toString().trim()
        viewModel.dispatch(AddPersonUiAction.FindUserEmail(person))
    }

    private fun setupRecycleView() {
        val spacing = resources.getDimensionPixelSize(com.wodox.core.R.dimen.dp_4)

        binding.rvItem.apply {
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            adapter = userAdapter
            addSpaceDecoration(spacing, true)
        }
    }


    companion object {
        fun newInstance() = AddPersonBottomSheet()
    }
}