package com.wodox.intro.ui.intro

import androidx.viewpager2.widget.ViewPager2
import com.osprey.core.extension.runDelayed
import com.wodox.common.navigation.AuthNavigator
import com.wodox.common.navigation.MainNavigator
import com.wodox.core.base.activity.BaseActivity
import com.wodox.core.extension.debounceClick
import com.wodox.core.extension.launchWhenStarted
import com.wodox.intro.R
import com.wodox.intro.databinding.ActivityIntroBinding
import com.wodox.intro.model.IntroData
import com.wodox.intro.ui.intro.tab.IntroPagerAdapter
import dagger.hilt.android.AndroidEntryPoint
import me.relex.circleindicator.CircleIndicator3
import javax.inject.Inject

@AndroidEntryPoint
class IntroActivity : BaseActivity<ActivityIntroBinding, IntroViewModel>(IntroViewModel::class) {

    @Inject
    lateinit var mainNavigator: MainNavigator

    @Inject
    lateinit var authNavigator: AuthNavigator
    private lateinit var introAdapter: IntroPagerAdapter

    override fun layoutId(): Int = R.layout.activity_intro

    override fun initialize() {
        binding.lifecycleOwner = this
        setupViewPager()
        setupActions()
        observer()
    }

    private fun setupViewPager() {
        val intros = IntroData.getDefault(this)
        introAdapter = IntroPagerAdapter(this, intros)
        binding.viewPager.adapter = introAdapter

        val indicator: CircleIndicator3 = binding.indicator
        indicator.setViewPager(binding.viewPager)
    }

    private fun setupActions() {
        binding.tvSkip.debounceClick {
            viewModel.dispatch(IntroUiAction.OnFinishIntro)
        }

        binding.tvContinue.debounceClick {
            val pager = binding.viewPager
            val nextIndex = pager.currentItem + 1
            if (nextIndex < introAdapter.itemCount) {
                pager.currentItem = nextIndex
            } else {
                viewModel.dispatch(IntroUiAction.OnFinishIntro)
            }
        }

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                viewModel.dispatch(IntroUiAction.OnPageChanged(position))
            }
        })
    }

    private fun observer() {
        launchWhenStarted {
            viewModel.uiEvent.collect { event ->
                when (event) {
                    IntroUiEvent.NavigateToMain -> showHome()
                }
            }
        }
    }

    private fun showHome() {
        runDelayed(100) {
            authNavigator.showWelcomeClear(this@IntroActivity)
            finish()
        }
    }
}
