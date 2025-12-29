package com.wodox.ui.task.userbottomsheet

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.wodox.core.extension.debounceClick
import com.wodox.domain.chat.model.UserWithFriendStatus
import com.wodox.core.base.fragment.BaseBottomSheetDialogFragment
import com.wodox.core.extension.addSpaceDecoration
import com.wodox.core.extension.screenHeight
import com.wodox.home.R
import com.wodox.home.databinding.FragmentListUserBottomSheetBinding
import dagger.hilt.android.AndroidEntryPoint
import java.util.UUID

@AndroidEntryPoint
class ListUserBottomSheet :
    BaseBottomSheetDialogFragment<FragmentListUserBottomSheetBinding, ListUserViewModel>(
        ListUserViewModel::class
    ) {
    var listener: OnItemClickListener? = null

    interface OnItemClickListener {
        fun onClick(id: UUID)
    }

    private val adapterUser by lazy {
        ListUserAdapter(
            context,
            object : ListUserAdapter.OnItemClickListener {
                override fun onClick(item: UserWithFriendStatus) {
                    listener?.onClick(item.user.id)
                }

            }
        )
    }

    override fun initialize() {
        setupUi()
        setupAction()
        setupRecycleViewPeople()
    }

    override fun layoutId(): Int = R.layout.fragment_list_user_bottom_sheet
    private fun setupUi() {
        binding.viewmodel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        binding.root.layoutParams = binding.root.layoutParams.apply {
            height = (requireActivity().screenHeight * 0.95).toInt()
        }
    }

    private fun setupAction() {
        binding.apply {
            toolbar.llClose.debounceClick {
                dismissAllowingStateLoss()
            }
        }
    }

    private fun setupRecycleViewPeople() {
        val spacing = resources.getDimensionPixelSize(com.wodox.core.R.dimen.dp_4)

        binding.rvListUser.apply {
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            adapter = adapterUser
            addSpaceDecoration(spacing, false)
        }
    }

    companion object {
        fun newInstance() = ListUserBottomSheet()
    }
}