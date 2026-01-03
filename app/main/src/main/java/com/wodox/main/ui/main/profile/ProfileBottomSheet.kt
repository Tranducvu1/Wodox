package com.wodox.main.ui.main.profile

import android.app.AlertDialog
import android.view.LayoutInflater
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.wodox.common.navigation.AuthNavigator
import com.wodox.common.navigation.CalendarNavigator
import com.wodox.common.navigation.MainNavigator
import com.wodox.core.base.fragment.BaseBottomSheetDialogFragment
import com.wodox.core.extension.addSpaceDecoration
import com.wodox.core.extension.debounceClick
import com.wodox.core.extension.launchWhenStarted
import com.wodox.core.extension.screenHeight
import com.wodox.core.extension.toast
import com.wodox.domain.main.model.Item
import com.wodox.domain.main.model.ItemTypeProfile
import com.wodox.main.R
import com.wodox.main.databinding.DialogHelpBinding
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

    @Inject
    lateinit var mainNavigator: MainNavigator

    @Inject
    lateinit var calendarNavigator: CalendarNavigator

    private val profileAdapter by lazy {
        ProfileAdapter(
            context, object : ProfileAdapter.OnItemClickListener {
                override fun onClick(item: Item) {
                    when (item.type) {
                        ItemTypeProfile.SIGN_OUT -> {
                            viewModel.dispatch(ProfileUiAction.SignOut)
                        }

                        ItemTypeProfile.MUTE -> {

                            handleToggleNotification()
                        }

                        ItemTypeProfile.HELP -> {
                            showSimpleHelpDialog()
                        }

                        ItemTypeProfile.MY_CALENDAR -> {
                            calendarNavigator.openMyCalendar(requireContext())
                        }

                        else -> {
                        }
                    }
                }

            })
    }


    private fun showSimpleHelpDialog() {
        val binding = DialogHelpBinding.inflate(LayoutInflater.from(requireContext()))

        val dialog = AlertDialog.Builder(requireContext()).setView(binding.root).create()

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        binding.apply {
            btnSendEmail.setOnClickListener {
                openEmail()
                dialog.dismiss()
            }

            btnClose.setOnClickListener {
                dialog.dismiss()
            }
        }

        dialog.show()
    }


    private fun openEmail() {
        val intent = android.content.Intent(android.content.Intent.ACTION_SENDTO).apply {
            data = android.net.Uri.parse("mailto:vutd.21it@vku.udn.vn")
            putExtra(android.content.Intent.EXTRA_SUBJECT, "Help Request - WodoxApp")
        }
        try {
            startActivity(intent)
        } catch (e: Exception) {
            requireContext().toast("No email app found")
        }
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
    }

    private fun setupAction() {
        binding.apply {
            toolbar.ivClose.debounceClick {
                dismissAllowingStateLoss()
            }
            btnEditProfile.debounceClick {
                mainNavigator.openEditActivity(requireContext())
            }
            tvAI.debounceClick {
                mainNavigator.openAIBottomSheet(childFragmentManager)
            }
        }
    }

    private fun handleToggleNotification() {
        launchWhenStarted {
            val currentState = viewModel.isNotificationEnabled.value
            val newState = !currentState
            viewModel.dispatch(ProfileUiAction.ToggleNotification(newState))
            val message = if (newState) {
                "🔔 Notification enabled - You will receive task deadline alerts"
            } else {
                "🔕 Notification disabled"
            }
            requireContext().toast(message)
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
        launchWhenStarted {
            viewModel.isNotificationEnabled.collect { enabled ->
                profileAdapter.updateNotificationState(enabled)
            }
        }
    }

    companion object {
        fun newInstance() = ProfileBottomSheet()
    }
}