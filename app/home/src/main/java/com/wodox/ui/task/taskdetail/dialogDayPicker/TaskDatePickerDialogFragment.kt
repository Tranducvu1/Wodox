package com.wodox.ui.task.taskdetail.dialogDayPicker

import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.wodox.core.extension.debounceClick
import com.wodox.core.base.fragment.BaseDialogFragment
import com.wodox.core.extension.getDialogWidth
import com.wodox.core.extension.showAllowingStateLoss
import com.wodox.core.extension.toast
import com.wodox.home.databinding.DialogTaskDatePickerFragmentBinding
import dagger.hilt.android.AndroidEntryPoint
import com.wodox.home.R
import com.wodox.ui.task.taskdetail.dialogNumberPicker.TaskTimePickerDialogFragment
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@AndroidEntryPoint
class TaskDatePickerDialogFragment :
    BaseDialogFragment<DialogTaskDatePickerFragmentBinding, TaskDatePickerViewModel>(
        TaskDatePickerViewModel::class
    ) {

    override fun layoutId() = R.layout.dialog_task_date_picker_fragment
    var onDateSelected: ((Long, Long) -> Unit)? = null

    var isSelectingStartDate = true
    var startDate: Long? = null
    var dueDate: Long? = null

    override fun initialize() {
        setSize(requireContext().getDialogWidth(), ViewGroup.LayoutParams.WRAP_CONTENT)

        binding.tvCancel.debounceClick { dismiss() }

        binding.tvToday.debounceClick {
            binding.calendarView.date = System.currentTimeMillis()
        }
        binding.tvTomorrow.debounceClick {
            binding.calendarView.date = System.currentTimeMillis() + 24 * 60 * 60 * 1000
        }
        binding.tvNextWeek.debounceClick {
            binding.calendarView.date = System.currentTimeMillis() + 7 * 24 * 60 * 60 * 1000
        }
        binding.tvWeekend.debounceClick {

        }

        binding.tvStartDate.debounceClick {
            isSelectingStartDate = true
            highlightStart()
        }

        binding.tvDueDate.debounceClick {
            isSelectingStartDate = false
            highlightDue()
        }
        binding.calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val cal = Calendar.getInstance().apply {
                set(year, month, dayOfMonth)
            }
            val date = cal.timeInMillis

            if (isSelectingStartDate) {
                startDate = date
                binding.tvStartDate.text = "Start: ${dayOfMonth}/${month + 1}/${year}"
                isSelectingStartDate = false
                highlightDue()
            } else {
                if (startDate != null && date < startDate!!) {
                    Toast.makeText(context, "Due date must be after start date", Toast.LENGTH_SHORT)
                        .show()
                    return@setOnDateChangeListener
                }
                dueDate = date
                binding.tvDueDate.text = "Due: ${dayOfMonth}/${month + 1}/${year}"
            }
        }

        binding.tvDone.debounceClick {

            if (startDate == null) {
                requireContext().toast("Please select start date")
                return@debounceClick
            }

            if (dueDate == null) {
                dueDate = startDate
            }

            if (dueDate!! < startDate!!) {
                requireContext().toast("End date must be after start date")
                return@debounceClick
            }

            onDateSelected?.invoke(startDate!!, dueDate!!)
            dismiss()
        }
        binding.tvAddTime.debounceClick {
            showTimePicker()
        }
    }

    private fun highlightStart() {
        binding.tvStartDate.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                com.wodox.resources.R.color.purple_600
            )
        )

        binding.tvDueDate.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                com.wodox.resources.R.color.color000000
            )
        )
    }

    private fun highlightDue() {
        binding.tvDueDate.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                com.wodox.resources.R.color.purple_600
            )
        )
        binding.tvStartDate.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                com.wodox.resources.R.color.color000000
            )
        )
    }

    private fun showTimePicker() {
        val timeDialog = TaskTimePickerDialogFragment.newInstance()
        timeDialog.onTimeSelected = { hour, minute ->
            val cal = Calendar.getInstance().apply {
                timeInMillis = if (isSelectingStartDate) startDate ?: System.currentTimeMillis()
                else dueDate ?: System.currentTimeMillis()

                set(Calendar.HOUR_OF_DAY, hour)
                set(Calendar.MINUTE, minute)
            }

            val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val formattedTime = timeFormat.format(cal.time)
            val formattedDate = dateFormat.format(cal.time)

            if (isSelectingStartDate) {
                startDate = cal.timeInMillis
                binding.tvStartDate.text = "Start: ${formattedDate} ${formattedTime}"
            } else {
                dueDate = cal.timeInMillis
                binding.tvDueDate.text = "Due: ${formattedDate} ${formattedTime}"
            }
        }
        timeDialog.showAllowingStateLoss(childFragmentManager)
    }

    companion object {
        fun newInstance() = TaskDatePickerDialogFragment()
    }
}
