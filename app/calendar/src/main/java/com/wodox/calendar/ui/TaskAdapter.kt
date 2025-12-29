package com.wodox.calendar.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.wodox.calendar.BR
import com.wodox.calendar.databinding.ItemTaskCalendarLayoutBinding
import com.wodox.common.ui.taskstatuspopup.TaskStatusPopup
import com.wodox.core.extension.debounceClick
import com.wodox.core.ui.adapter.TMVVMAdapter
import com.wodox.core.ui.adapter.TMVVMViewHolder
import com.wodox.domain.home.model.local.Task
import com.wodox.domain.home.model.local.TaskStatus
import java.text.SimpleDateFormat
import java.util.Locale

class TaskCalendarAdapter(
    private val context: Context,
    private val listener: OnItemClickListener
) : TMVVMAdapter<Task>(ArrayList()) {

    interface OnItemClickListener {
        fun onStatusChanged(task: Task, newStatus: TaskStatus)
    }

    override fun onCreateViewHolderBase(
        parent: ViewGroup?,
        viewType: Int
    ): TMVVMViewHolder {
        val binding =
            ItemTaskCalendarLayoutBinding.inflate(
                LayoutInflater.from(parent?.context),
                parent,
                false
            )
        return TMVVMViewHolder(binding)
    }

    override fun onBindViewHolderBase(holder: TMVVMViewHolder?, position: Int) {
        val task = list.getOrNull(position) ?: return
        val binding = holder?.binding as ItemTaskCalendarLayoutBinding
        binding.cState.debounceClick { view ->
            TaskStatusPopup.show(context, binding.lllayout) { newStatus ->
                val updatedTask = task.copy(status = newStatus)
                listener.onStatusChanged(task, newStatus)
                binding.setVariable(BR.task, updatedTask)
                binding.executePendingBindings()
            }
        }
        binding.setVariable(BR.task, task)
        binding.executePendingBindings()
    }

    override fun submitList(newList: List<Task>) {
        list.clear()
        list.addAll(newList)
        notifyDataSetChanged()
    }


    companion object {

        @JvmStatic
        @BindingAdapter("app:formatDateRange")
        fun TextView.setDateRange(task: Task) {
            val format = SimpleDateFormat("HH:mm dd/MM", Locale.getDefault())

            val start = task.startAt?.let { format.format(it) } ?: ""
            val end = task.dueAt?.let { format.format(it) } ?: ""

            text = when {
                start.isNotEmpty() && end.isNotEmpty() -> "⏰ $start → $end"
                end.isNotEmpty() -> "⏰ $end"
                else -> ""
            }
        }

    }
}
