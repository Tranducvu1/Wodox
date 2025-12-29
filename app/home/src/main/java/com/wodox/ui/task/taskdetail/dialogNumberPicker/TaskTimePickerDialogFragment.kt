package com.wodox.ui.task.taskdetail.dialogNumberPicker

import android.view.ViewGroup
import com.wodox.core.base.fragment.BaseDialogFragment
import com.wodox.core.extension.debounceClick
import com.wodox.core.extension.dialogWidth
import com.wodox.home.R
import com.wodox.home.databinding.DialogTaskTimePickerFragmentBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TaskTimePickerDialogFragment :
    BaseDialogFragment<DialogTaskTimePickerFragmentBinding, TaskTimePickerViewModel>(TaskTimePickerViewModel::class) {
    var onTimeSelected: ((Int, Int) -> Unit)? = null

    override fun layoutId() = R.layout.dialog_task_time_picker_fragment

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    override fun initialize() {
        setupUI()
        setupAction()
    }

    private fun setupUI() {

    }

    private fun setupAction() {
        binding.btnDone.debounceClick {
            val hour = binding.hourPicker.value
            val minute = binding.minutePicker.value
            onTimeSelected?.invoke(hour, minute)
            dismiss()
        }
        binding.btnClose.debounceClick { dismiss() }
    }

    companion object {
        @JvmStatic
        fun newInstance() = TaskTimePickerDialogFragment()
    }
}