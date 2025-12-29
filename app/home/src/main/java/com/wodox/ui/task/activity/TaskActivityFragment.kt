package com.wodox.ui.task.activity

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.wodox.common.navigation.MainNavigator
import com.wodox.core.base.fragment.BaseFragment
import com.wodox.core.extension.addSpaceDecoration
import com.wodox.core.extension.launchWhenStarted
import com.wodox.domain.home.model.local.Task
import com.wodox.home.R
import com.wodox.home.databinding.FragmentHomeLayoutBinding
import com.wodox.ui.home.HomeViewModel
import com.wodox.ui.home.TaskAdapter
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class TaskActivityFragment : BaseFragment<FragmentHomeLayoutBinding, HomeViewModel>(
    HomeViewModel::class
) {
    @Inject
    lateinit var mainNavigator: MainNavigator
    private val adapterPagingTask by lazy {
        TaskAdapter(
            context,
            object : TaskAdapter.OnItemClickListener {
                override fun onClick(
                    task: Task
                ) {

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
            viewModel.taskFlow.collect { list ->
                adapterPagingTask.submitData(list)
            }
        }
    }


    private fun setupFlashCard() {
        binding.rvTask.apply {
            adapter = adapterPagingTask
        }
    }

    companion object {
        fun newInstance() = TaskActivityFragment()
    }
}
