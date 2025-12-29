package com.wodox.main.ui.main.profile

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.wodox.common.navigation.AuthNavigator
import com.wodox.core.base.fragment.BaseBottomSheetDialogFragment
import com.wodox.core.extension.addSpaceDecoration
import com.wodox.core.extension.debounceClick
import com.wodox.core.extension.launchWhenStarted
import com.wodox.core.extension.screenHeight
import com.wodox.core.extension.toast
import com.wodox.domain.main.model.Item
import com.wodox.domain.main.model.ItemTypeProfile
import com.wodox.main.R
import com.wodox.main.databinding.FragmentProfileBottomSheetBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ProfileBottomSheet :
    BaseBottomSheetDialogFragment<FragmentProfileBottomSheetBinding, ProfileViewModel>(
        ProfileViewModel::class
    ) {

    @Inject
    lateinit var authNavigator: AuthNavigator

    private val profileAdapter by lazy {
        ProfileAdapter(
            context,
            object : ProfileAdapter.OnItemClickListener {
                override fun onClick(item: Item) {
                    when (item.type) {
                        ItemTypeProfile.SIGN_OUT -> {
                            viewModel.dispatch(ProfileUiAction.SignOut)
                        }

                        else -> {}
                    }
                }

            }
        )
    }

    override fun initialize() {
        setupUi()
        setupAction()
        setupRecycleView()
        observer()
    }

    private fun setupUi() {
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        binding.root.layoutParams = binding.root.layoutParams.apply {
            height = (requireActivity().screenHeight * 100)
        }
    }

    private fun setupAction() {
        binding.apply {
            toolbar.ivClose.debounceClick {
                dismissAllowingStateLoss()
            }
        }
    }

    private fun setupRecycleView() {
        val spacing = resources.getDimensionPixelSize(com.wodox.core.R.dimen.dp_4)

        binding.rvItems.apply {
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            adapter = profileAdapter
            addSpaceDecoration(spacing, true)
        }
    }

    override fun layoutId(): Int = R.layout.fragment_profile_bottom_sheet

    private fun observer() {
        launchWhenStarted {
            viewModel.uiEvent.collect { event ->
                when (event) {
                    ProfileUiEvent.NavigateSignOut -> {
                        requireContext().toast("Sign out Successfull")
                        authNavigator.showSignIn(requireContext(), false)
                    }
                }
            }
        }
    }

    companion object {
        fun newInstance() = ProfileBottomSheet()
    }
}