package com.wodox.setting.ui

import android.content.Intent
import android.net.Uri
import androidx.recyclerview.widget.LinearLayoutManager
import com.wodox.common.navigation.SettingNavigator
import com.wodox.core.base.fragment.BaseFragment
import com.wodox.core.extension.addSpaceDecoration
import com.wodox.core.extension.launchWhenStarted
import com.wodox.setting.R
import com.wodox.setting.databinding.FragmentSettingLayoutBinding
import com.wodox.setting.model.SettingItem
import com.wodox.setting.model.SettingItemType
import com.wodox.ui.setting.SettingViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SettingFragment : BaseFragment<FragmentSettingLayoutBinding, SettingViewModel>(
    SettingViewModel::class
) {

    @Inject
    lateinit var settingNavigator: SettingNavigator

    private val settingAdapter by lazy {
        SettingAdapter(
            context,
            object : SettingAdapter.OnItemClickListener {
                override fun onClick(item: SettingItem) {
                    handleSettingItemClick(item)
                }
            }
        )
    }

    override fun layoutId(): Int = R.layout.fragment_setting_layout

    override fun initialize() {
        setupUI()
        setupAction()
        observe()
    }

    private fun setupUI() {
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        val spacing = resources.getDimensionPixelSize(com.wodox.core.R.dimen.dp_12)

        binding.rvSettings.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = settingAdapter
            addSpaceDecoration(spacing, false)
        }

        viewModel.dispatch(SettingUiAction.LoadSettings)
    }

    private fun setupAction() {
        binding.apply {
            // Handle promotion banner click
            cvPromotionBanner.setOnClickListener {
                this@SettingFragment.viewModel.dispatch(SettingUiAction.ClaimPromotion)
            }

            btnClaim.setOnClickListener {
                this@SettingFragment.viewModel.dispatch(SettingUiAction.ClaimPromotion)
            }
        }
    }

    private fun observe() {
        launchWhenStarted {
            viewModel.uiState.collect { state ->
                settingAdapter.submitList(state.settingItems)
            }
        }
    }

    private fun handleSettingItemClick(item: SettingItem) {
        when (item.type) {
            SettingItemType.NOTIFICATION -> {
                settingNavigator.openNotifications(requireContext())
            }
            SettingItemType.PASSWORD -> {
                settingNavigator.openChangePassword(requireContext())
            }
            SettingItemType.LANGUAGE -> {
                settingNavigator.openLanguage(requireContext())
            }
            SettingItemType.RATE_US -> {
                openPlayStore()
            }
            SettingItemType.EXPLORE_APPS -> {
                openDeveloperPage()
            }
            SettingItemType.PRIVACY -> {
                settingNavigator.openPrivacyPolicy(requireContext())
            }
        }
    }

    private fun openPlayStore() {
        try {
            val packageName = requireContext().packageName
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("market://details?id=$packageName")
            }
            startActivity(intent)
        } catch (e: Exception) {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("https://play.google.com/store/apps/details?id=${requireContext().packageName}")
            }
            startActivity(intent)
        }
    }

    private fun openDeveloperPage() {
        try {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("market://developer?id=YourDeveloperName")
            }
            startActivity(intent)
        } catch (e: Exception) {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("https://play.google.com/store/apps/developer?id=YourDeveloperName")
            }
            startActivity(intent)
        }
    }

    companion object {
        fun newInstance() = SettingFragment()
    }
}