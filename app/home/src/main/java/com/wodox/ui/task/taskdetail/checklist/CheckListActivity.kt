package com.wodox.ui.task.taskdetail.checklist

import android.view.inputmethod.EditorInfo
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.wodox.core.extension.debounceClick
import com.wodox.core.extension.gone
import com.wodox.core.extension.show
import com.wodox.core.base.activity.BaseActivity
import com.wodox.core.extension.addSpaceDecoration
import com.wodox.core.extension.launchWhenStarted
import com.wodox.core.extension.toast
import com.wodox.core.util.hideKeyboard
import com.wodox.core.util.showKeyboard
import com.wodox.domain.home.model.local.CheckList
import com.wodox.home.R
import com.wodox.home.databinding.ActivityCheckListLayoutBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CheckListActivity : BaseActivity<ActivityCheckListLayoutBinding, CheckListViewModel>(
    CheckListViewModel::class
) {

    private val adapterPagingTask by lazy {
        CheckListAdapter(
            applicationContext,
            object : CheckListAdapter.OnItemClickListener {
                override fun onClick(checkList: CheckList) {
                    TODO("Not yet implemented")
                }

                override fun onDeleteClick(checkList: CheckList) {
                    TODO("Not yet implemented")
                }
            }
        )
    }

    override fun layoutId(): Int = R.layout.activity_check_list_layout

    override fun initialize() {
        setupUI()
        setupAction()
        setupRecyclerview()
        observe()
    }


    private fun setupUI() {
        binding.lifecycleOwner = this
        val spacing = resources.getDimensionPixelSize(com.wodox.core.R.dimen.dp_6)

        binding.rvCheckList.apply {
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
            ivBack.debounceClick {
                finish()
            }
            tvEmptyState.debounceClick {
                showInputContainer()
            }
            btnAddItem.debounceClick {
                showInputContainer()
            }
            ivConfirm.debounceClick {
                addNewCheckList()
            }
            etNewItem.setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    addNewCheckList()
                    true
                } else {
                    false
                }
            }
        }
    }

    private fun showInputContainer() {
        binding.apply {
            cardInputContainer.show()
            etNewItem.requestFocus()
            etNewItem.showKeyboard()
        }
    }


    private fun hideInputContainer() {
        binding.apply {
            cardInputContainer.gone()
            etNewItem.setText("")
            etNewItem.hideKeyboard()
        }
    }

    private fun addNewCheckList() {
        val text = binding.etNewItem.text?.toString()?.trim()
        if (!text.isNullOrEmpty()) {
            viewModel.dispatch(CheckListUiAction.AddNewDescription(text))
            hideInputContainer()
        }
    }

    private fun observe() {
        launchWhenStarted {
            viewModel.uiEvent.collect { event ->
                when (event) {
                    CheckListUiEvent.SuccessUpdate -> {
                        toast("Add Successfull")
                    }
                }
            }
        }

        launchWhenStarted {
            viewModel.uiState.collect { state ->
                adapterPagingTask.submitList(state.checkList)
            }
        }
    }

    private fun setupRecyclerview() {
        binding.rvCheckList.apply {
            adapter = adapterPagingTask
        }
    }
}