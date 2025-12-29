package com.wodox.ui.favourite

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.wodox.common.model.SearchEvent
import com.wodox.common.navigation.HomeNavigator
import com.wodox.core.base.fragment.BaseFragment
import com.wodox.core.extension.addSpaceDecoration
import com.wodox.core.extension.launchWhenStarted
import com.wodox.core.extension.show
import com.wodox.domain.home.model.local.Task
import com.wodox.home.R
import com.wodox.home.databinding.FragmentFavouriteLayoutBinding
import com.wodox.ui.home.HomeUiAction
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import javax.inject.Inject

@AndroidEntryPoint
class FavouriteFragment : BaseFragment<FragmentFavouriteLayoutBinding, FavouriteViewModel>(
    FavouriteViewModel::class
) {
    override fun layoutId(): Int = R.layout.fragment_favourite_layout

    private var currentSearchQuery: String = ""

    @Inject
    lateinit var homeNavigator: HomeNavigator
    private val adapterPagingTask by lazy {
        TaskFavouriteAdapter(
            context,
            object : TaskFavouriteAdapter.OnItemClickListener {
                override fun onMenuClick(task: Task) {
                    homeNavigator.openTask(requireContext(), task)
                }
            }
        )
    }

    override fun initialize() {
        setupUI()
        setupAction()
        setupFlashCard()
        observe()
    }

    private fun setupUI() {
        binding.lifecycleOwner = this
        val spacing = resources.getDimensionPixelSize(com.wodox.core.R.dimen.dp_6)

        binding.rvTask.apply {
            layoutManager = LinearLayoutManager(
                context,
                RecyclerView.VERTICAL,
                false
            )
            adapter = adapterPagingTask
            addSpaceDecoration(spacing, false)
        }
    }

    override fun onStart() {
        super.onStart()
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)
            android.util.Log.d("HomeFragment", "EventBus registered")
        }
    }

    override fun onStop() {
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this)
        }
        super.onStop()
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onSearchEvent(event: SearchEvent) {
        android.util.Log.d("HomeFragment", "Received SearchEvent: ${event.query}")
        currentSearchQuery = event.query
        viewModel.dispatch(HomeUiAction.UpdateSearchQuery(event.query))
    }

    private fun setupAction() {
        binding.apply {
            adapterPagingTask.addLoadStateListener { loadStates ->
                val isEmpty = adapterPagingTask.itemCount == 0 &&
                        loadStates.refresh is androidx.paging.LoadState.NotLoading
                binding.llEmpty.show(isEmpty)
                binding.rvTask.show(!isEmpty)
            }
        }
    }


    private fun observe() {
        launchWhenStarted {
            viewModel.taskFlow.collect { list ->
                adapterPagingTask.submitData(list)
                adapterPagingTask.addLoadStateListener { loadStates ->
                    val isEmpty = adapterPagingTask.itemCount == 0 &&
                            loadStates.refresh is androidx.paging.LoadState.NotLoading
                    binding.llEmpty.show(isEmpty)
                    binding.rvTask.show(!isEmpty)
                }
            }
        }
    }


    private fun setupFlashCard() {
        binding.rvTask.apply {
            adapter = adapterPagingTask
        }
    }

    //    private fun showConfirmDelete(flashcard: FlashCard) {
//        showDefaultDialog(
//            supportFragmentManager,
//            task = getString(com.starnest.resources.R.string.confirm_delete),
//            message = getString(com.starnest.resources.R.string.are_you_sure_you_want_to_delete_these_entries_this_action_cannot_be_undone),
//            positiveTitle = getString(com.starnest.core.R.string.delete),
//            positiveCallback = {
//                viewModel.dispatch(FlashCardUiAction.DeleteFlashCard(flashcard))
//            },
//            negativeTitle = getString(com.starnest.core.R.string.cancel),
//            isDeleteDialog = true
//        )
//    }
//
//    private fun editFlashCard(task: FlashCard) {
//        val bottomSheet = EditFlashCardBottomSheet.newInstance(task)
//        bottomSheet.listener =
//            object : EditFlashCardBottomSheet.OnItemClickListener {
//                override fun onUpdateFlashCard(task: FlashCard) {
//                    viewModel.dispatch(FlashCardUiAction.UpdateFlashCard(task))
//                }
//            }
//        bottomSheet.showAllowingStateLoss(supportFragmentManager)
//    }

    companion object {
        fun newInstance() = FavouriteFragment()
    }
}