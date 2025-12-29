package com.wodox.main.ui.main

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import com.wodox.core.extension.debounceClick
import com.wodox.common.navigation.IntroNavigator
import com.wodox.main.R
import com.wodox.main.databinding.ActivityMainBinding
import com.wodox.main.model.Constants
import com.wodox.common.model.SearchEvent
import com.wodox.common.navigation.MainNavigator
import com.wodox.core.base.activity.BaseActivity
import com.wodox.core.extension.gone
import com.wodox.core.extension.launchWhenStarted
import com.wodox.core.extension.show
import com.wodox.main.ui.main.MainUiAction.ChangeTab
import com.wodox.main.ui.main.MainUiAction.ChangeTopTab
import com.wodox.main.ui.main.bottombar.BottomBarMenu
import com.wodox.main.ui.main.bottombar.BottomBarView
import com.wodox.main.ui.main.topbar.TopBarMenu
import com.wodox.main.ui.main.topbar.TopBarView
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding, MainViewModel>(MainViewModel::class) {
    override fun layoutId(): Int = R.layout.activity_main

    @Inject
    lateinit var mainNavigator: MainNavigator
    private val pagerBottomAdapter by lazy(LazyThreadSafetyMode.NONE) {
        MainBottomPagerAdapter(this)
    }

    private val pagerTopAdapter by lazy(LazyThreadSafetyMode.NONE) {
        MainTopPagerAdapter(this)
    }

    private var isFirstLaunch = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            isFirstLaunch = intent.getBooleanExtra(Constants.Intents.IS_FIRST_LAUNCH, false)
        }
    }

    override fun initialize() {
        setupViewPagers()
        setupBottomBar()
        setupTopBar()
        observeViewModel()
        showTopBarMode()
        observer()
        setupUI()
        setupAction()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
    }


    private fun setupAction() {
        binding.apply {
            toolbarHeader.edtSearch.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

                override fun afterTextChanged(s: Editable?) {
                    val query = s?.toString()?.trim() ?: ""
                    EventBus.getDefault().postSticky(SearchEvent(query))
                }
            })

            toolbarHeader.ivAdd.debounceClick {
                mainNavigator.showAddPerson(supportFragmentManager)
            }
            toolbarHeader.avatarContainer.debounceClick {
                mainNavigator.showProfile(supportFragmentManager)
            }
        }
    }

    private fun setupUI() {
        binding.viewModel = viewModel
        val user = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser
        val name = user?.displayName ?: "User"
        binding.toolbarHeader.tvWorkspace.text = "${name}’s Workspace"
    }

    private fun observer() {
        launchWhenStarted {
            viewModel.uiState.collect {
                val email = it.email ?: ""
                binding.toolbarHeader.tvAvatarLetter.text = email.firstOrNull()?.uppercase()
            }
        }
    }

    private fun setupViewPagers() {
        binding.viewPagerTop.run {
            adapter = pagerTopAdapter
            offscreenPageLimit = 5
            isUserInputEnabled = false
        }

        binding.viewPagerBottom.run {
            adapter = pagerBottomAdapter
            offscreenPageLimit = 3
            isUserInputEnabled = false
        }
    }

    private fun setupBottomBar() {
        binding.bottomBarView.listener = object : BottomBarView.OnBottomBarViewListener {
            override fun onClick(menu: BottomBarMenu) {
                when (menu.type) {
                    BottomBarMenu.BottomBarMenuType.HOME -> {
                        showTopBarMode()
                        binding.viewPagerBottom.setCurrentItem(0, false)
                        viewModel.dispatch(ChangeTab(BottomBarMenu.BottomBarMenuType.HOME))
                    }

                    BottomBarMenu.BottomBarMenuType.ACTIVITY -> {
                        showBottomBarMode()
                        binding.viewPagerBottom.setCurrentItem(1, false)
                        viewModel.dispatch(ChangeTab(BottomBarMenu.BottomBarMenuType.ACTIVITY))
                    }

                    BottomBarMenu.BottomBarMenuType.CREATE -> {
                        showBottomBarMode()
                        binding.viewPagerBottom.setCurrentItem(2, false)
                        viewModel.dispatch(ChangeTab(BottomBarMenu.BottomBarMenuType.CREATE))
                    }

                    BottomBarMenu.BottomBarMenuType.MY_WORK -> {
                        showBottomBarMode()
                        binding.viewPagerBottom.setCurrentItem(3, false)
                        viewModel.dispatch(ChangeTab(BottomBarMenu.BottomBarMenuType.MY_WORK))
                    }
                }
            }
        }
    }

    private fun setupTopBar() {
        binding.topbarView.listener = object : TopBarView.OnTopBarViewListener {
            override fun onClick(menu: TopBarMenu) {
                when (menu.type) {
                    TopBarMenu.TopBarMenuType.RECENT -> {
                        binding.viewPagerTop.setCurrentItem(0, false)
                        viewModel.dispatch(ChangeTopTab(TopBarMenu.TopBarMenuType.RECENT))
                    }

                    TopBarMenu.TopBarMenuType.FAVOURITE -> {
                        binding.viewPagerTop.setCurrentItem(1, false)
                        viewModel.dispatch(ChangeTopTab(TopBarMenu.TopBarMenuType.FAVOURITE))
                    }

                    TopBarMenu.TopBarMenuType.CALENDER -> {
                        binding.viewPagerTop.setCurrentItem(2, false)
                        viewModel.dispatch(ChangeTopTab(TopBarMenu.TopBarMenuType.CALENDER))
                    }

                    TopBarMenu.TopBarMenuType.DOCS -> {
                        binding.viewPagerTop.setCurrentItem(3, false)
                        viewModel.dispatch(ChangeTopTab(TopBarMenu.TopBarMenuType.DOCS))
                    }

                    TopBarMenu.TopBarMenuType.MY_WORK -> {
                        binding.viewPagerTop.setCurrentItem(4, false)
                        viewModel.dispatch(ChangeTopTab(TopBarMenu.TopBarMenuType.MY_WORK))
                    }
                }
            }
        }
    }

    private fun observeViewModel() {
        viewModel.changePageEvent.observe(this) { page ->
            binding.viewPagerTop.setCurrentItem(page, false)
        }

        viewModel.changePageBottomEvent.observe(this) { page ->
            binding.viewPagerBottom.setCurrentItem(page, false)
        }
    }

    private fun showTopBarMode() {
        binding.topbarView.show()
        binding.viewPagerTop.show()
        binding.viewPagerBottom.gone()
    }

    private fun showBottomBarMode() {
        binding.topbarView.gone()
        binding.viewPagerTop.gone()
        binding.viewPagerBottom.show()
    }

//    @Subscribe(threadMode = ThreadMode.MAIN)
//    fun onLanguage(event: LanguageEvent) {
//        val codes = event.language.split("-")
//        val language = codes.getOrNull(0) ?: return
//        val country = codes.getOrNull(1)
//        applicationContext.changeLanguage(language, country)
//        baseContext.changeLanguage(language, country)
//        changeLanguage(language, country)
//        viewModel.context?.get()?.changeLanguage(language, country)
//        runDelayed(20) {
//            introNavigator.openSplash(this)
//            finish()
//        }
//    }
}