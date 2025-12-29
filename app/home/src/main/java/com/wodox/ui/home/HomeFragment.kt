package com.wodox.ui.home

import android.util.Log
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.wodox.core.extension.show
import com.wodox.common.navigation.HomeNavigator
import com.wodox.domain.home.model.local.Task
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import com.wodox.home.R
import com.wodox.home.databinding.FragmentHomeLayoutBinding
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import com.wodox.common.model.SearchEvent
import com.wodox.core.base.fragment.BaseFragment
import com.wodox.core.extension.addSpaceDecoration
import com.wodox.core.extension.launchWhenStarted
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeLayoutBinding, HomeViewModel>(HomeViewModel::class) {
    @Inject
    lateinit var homeNavigator: HomeNavigator

    private var currentSearchQuery: String = ""

    private val adapterPagingTask by lazy {
        TaskAdapter(
            context,
            object : TaskAdapter.OnItemClickListener {
                override fun onClick(task: Task) {
                    homeNavigator.openTask(requireContext(), task)
                }
            }
        )
    }

    override fun layoutId(): Int = R.layout.fragment_home_layout

    override fun initialize() {
        setupUI()
        setupAction()
        setupFlashCard()
        observe()
        EventBus.getDefault().getStickyEvent(SearchEvent::class.java)?.let { event ->
            currentSearchQuery = event.query
            viewModel.dispatch(HomeUiAction.UpdateSearchQuery(event.query))
        }
    }

    override fun onStart() {
        super.onStart()
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)
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
        currentSearchQuery = event.query
        viewModel.dispatch(HomeUiAction.UpdateSearchQuery(event.query))
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

    private fun setupAction() {
        binding.apply {
        }
    }

    private fun observe() {
        launchWhenStarted {
            viewModel.taskFlow.collectLatest { pagingData ->
                Log.d("HomeFragment", "🔵 Received PagingData from ViewModel")

                // ✅ Log dữ liệu TRƯỚC khi submit
                logPagingData(pagingData)

                adapterPagingTask.submitData(pagingData)
            }
        }

        adapterPagingTask.addLoadStateListener { loadStates ->
            val isEmpty = adapterPagingTask.itemCount == 0 &&
                    loadStates.refresh is androidx.paging.LoadState.NotLoading

            Log.d("HomeFragment", "📋 Adapter itemCount: ${adapterPagingTask.itemCount}")
            Log.d("HomeFragment", "📋 isEmpty: $isEmpty")

            // ✅ Log dữ liệu SAU khi submit vào adapter
            if (!isEmpty) {
                logAdapterData()
            }

            binding.llEmpty.show(isEmpty)
            binding.rvTask.show(!isEmpty)
        }
    }

    // ✅ Hàm log PagingData
    private fun logPagingData(pagingData: PagingData<Task>) {
        Log.d("HomeFragment", """
            ═══════════════════════════════════════════════════
            📊 PAGING DATA CONTENT:
        """.trimIndent())

        var index = 0
        // Chúng ta không thể iterate trực tiếp qua PagingData,
        // nhưng có thể log qua adapter sau khi submitData
    }

    // ✅ Hàm log dữ liệu từ Adapter (sau khi load)
    private fun logAdapterData() {
        Log.d("HomeFragment", """
            ═══════════════════════════════════════════════════
            📋 ADAPTER FINAL DATA (in display order):
        """.trimIndent())

        for (i in 0 until adapterPagingTask.itemCount) {
            try {
                val task = adapterPagingTask.peek(i)
                if (task != null) {
                    Log.d("HomeFragment",
                        "  #${i + 1}: '${task.title}' | Priority: %.2f | ID: ${task.id}".format(task.calculatedPriority)
                    )
                } else {
                    Log.d("HomeFragment", "  #${i + 1}: [Loading...]")
                }
            } catch (e: Exception) {
                Log.e("HomeFragment", "Error getting item at $i: ${e.message}")
            }
        }

        Log.d("HomeFragment", """
            ═══════════════════════════════════════════════════
        """.trimIndent())
    }

    private fun setupFlashCard() {
        binding.rvTask.apply {
            adapter = adapterPagingTask
        }
    }

    companion object {
        fun newInstance() = HomeFragment()
    }
}