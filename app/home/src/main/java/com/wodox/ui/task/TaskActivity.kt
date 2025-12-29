package com.wodox.ui.task

import com.wodox.common.navigation.HomeNavigator
import com.wodox.core.base.activity.BaseActivity
import com.wodox.core.extension.debounceClick
import com.wodox.core.extension.launchWhenStarted
import com.wodox.home.R
import com.wodox.home.databinding.ActivityTaskLayoutBinding
import com.wodox.ui.task.menu.TaskBarMenu
import com.wodox.ui.task.menu.TaskBarView
import com.wodox.ui.task.menuoption.OptionMenuBottomSheet
import com.wodox.ui.task.taskdetail.TaskDetailFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class TaskActivity : BaseActivity<ActivityTaskLayoutBinding, TaskViewModel>(TaskViewModel::class) {

    override fun layoutId(): Int = R.layout.activity_task_layout

    @Inject
    lateinit var homeNavigator: HomeNavigator

    private val pagerTopAdapter by lazy(LazyThreadSafetyMode.NONE) {
        TaskPagerAdapter(this, viewModel.task)
    }

    override fun initialize() {
        setupViewPagers()
        setupBottomBar()
        observeViewModel()
        setupUI()
        setupAction()
        observer()
    }

    private fun setupUI() {
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        checkIsFavourite()
    }

    private fun checkIsFavourite() {
        val check = viewModel.uiState.value.task?.isFavourite ?: return
        binding.apply {
            if (check) {
                toolbar.ivFavourite.setImageResource(com.wodox.resources.R.drawable.ic_favorite_selection)
            } else {
                toolbar.ivFavourite.setImageResource(com.wodox.resources.R.drawable.ic_favorite_unselection)
            }
        }
    }

    private fun setupAction() {
        binding.apply {
            toolbar.btnBack.debounceClick {
                finish()
            }
            toolbar.btnFavorite.debounceClick {
                this@TaskActivity.viewModel.dispatch(TaskUiAction.HandleUpdateFavourite)
            }
            toolbar.btnShare.debounceClick {
                val currentItem = binding.viewPagerTop.currentItem

                val fragment = pagerTopAdapter.getFragment(currentItem)

                if (fragment is TaskDetailFragment) {

                    fragment.handleAskAI()
                }
            }
            binding.toolbar.btnMore.debounceClick {
                showOptionMenu()
            }

        }
    }

    private fun showOptionMenu() {
        val task = viewModel.task
        if (task != null) {
            val bottomSheet = OptionMenuBottomSheet.newInstance(task)
            bottomSheet.taskDeleteListener = object : OptionMenuBottomSheet.TaskDeleteListener {
                override fun onTaskDeleted() {
                    finish()
                }
            }
            bottomSheet.show(supportFragmentManager, "OptionMenuBottomSheet")
        }
    }

    private fun setupViewPagers() {
        binding.viewPagerTop.run {
            adapter = pagerTopAdapter
            offscreenPageLimit = 2
            isUserInputEnabled = false
        }
    }

    private fun setupBottomBar() {
        binding.taskbarView.listener = object : TaskBarView.OnTaskBarViewListener {
            override fun onClick(menu: TaskBarMenu) {
                when (menu.type) {
                    TaskBarMenu.TaskBarMenuType.DETAIL -> {
                        viewModel.dispatch(TaskUiAction.ChangeTab(TaskBarMenu.TaskBarMenuType.DETAIL))
                    }

                    TaskBarMenu.TaskBarMenuType.ACTIVITY -> {
                        viewModel.dispatch(TaskUiAction.ChangeTab(TaskBarMenu.TaskBarMenuType.ACTIVITY))
                    }
                }
            }
        }
    }


    private fun observeViewModel() {
        viewModel.changePageEvent.observe(this) { page ->
            binding.viewPagerTop.setCurrentItem(page, false)
        }
    }

    private fun observer() {
        launchWhenStarted {
            viewModel.uiEvent.collect { event ->
                when (event) {
                    TaskUiEvent.UpdateFavourite -> checkIsFavourite()
                }
            }
        }
    }
}