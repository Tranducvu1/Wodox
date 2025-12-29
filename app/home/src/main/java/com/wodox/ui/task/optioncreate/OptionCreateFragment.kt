package com.wodox.ui.task.optioncreate

import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.wodox.common.navigation.HomeNavigator
import com.wodox.core.base.fragment.BaseFragment
import com.wodox.core.extension.addSpaceDecoration
import com.wodox.domain.home.model.local.Item
import com.wodox.domain.home.model.local.ItemType
import com.wodox.home.R
import com.wodox.home.databinding.FragmentCreateOptionLayoutBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class OptionCreateFragment :
    BaseFragment<FragmentCreateOptionLayoutBinding, OptionCreateViewModel>(
        OptionCreateViewModel::class) {
    @Inject
    lateinit var homeNavigator: HomeNavigator

    private val subTaskAdapter by lazy {
        ItemAdapter(
            context,
            object : ItemAdapter.OnItemClickListener {
                override fun onClick(item: Item) {
                    when (item.type) {
                        ItemType.TASK -> {
                            homeNavigator.openCreateTask(childFragmentManager)
                        }
                        ItemType.CHANNEL -> {

                        }
                        ItemType.CHAT -> {

                        }
                        else -> {

                        }
                    }
                }
            }
        )
    }

    override fun initialize() {
        setupUi()
    }

    override fun layoutId(): Int = R.layout.fragment_create_option_layout

    private fun setupUi() {
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        setupRecycleView()
    }

    private fun setupRecycleView() {
        val spacing = resources.getDimensionPixelSize(com.wodox.core.R.dimen.dp_4)

        binding.rvItem.apply {
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            adapter = subTaskAdapter
            addSpaceDecoration(spacing, true)
        }
    }


    companion object {
        fun newInstance() = OptionCreateFragment()
    }
}