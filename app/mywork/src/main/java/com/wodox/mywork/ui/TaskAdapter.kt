package com.wodox.mywork.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.wodox.core.extension.show
import com.wodox.core.ui.adapter.TMVVMAdapter
import com.wodox.core.ui.adapter.TMVVMViewHolder
import com.wodox.domain.home.model.local.Task
import com.wodox.mywork.BR
import com.wodox.mywork.databinding.ItemTaskWorkLayoutBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class TaskAdapter(
    private val context: Context?,
    private val listener: OnItemClickListener
) : TMVVMAdapter<Task>(ArrayList()) {

    interface OnItemClickListener

    override fun onCreateViewHolderBase(
        parent: ViewGroup?,
        viewType: Int
    ): TMVVMViewHolder {
        val binding = ItemTaskWorkLayoutBinding.inflate(
            LayoutInflater.from(parent?.context),
            parent,
            false
        )
        return TMVVMViewHolder(binding)
    }

    override fun onBindViewHolderBase(
        holder: TMVVMViewHolder?,
        position: Int
    ) {
        val task = list.getOrNull(position) ?: return
        val binding = holder?.binding as ItemTaskWorkLayoutBinding
        binding.setVariable(BR.task, task)
        binding.executePendingBindings()
        binding.dateBadge.show(task.isFirstOfDay)
    }

    companion object {
        @JvmStatic
        @BindingAdapter("app:formatDated")
        fun setFormattedDate(textView: TextView, date: Date?) {
            if (date != null) {
                val calendar = Calendar.getInstance()
                val currentYear = calendar.get(Calendar.YEAR)
                val dateCalendar = Calendar.getInstance().apply { time = date }
                val yearOfDate = dateCalendar.get(Calendar.YEAR)

                val format = if (yearOfDate == currentYear) {
                    SimpleDateFormat("MMM dd", Locale.getDefault())
                } else {
                    SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                }
                textView.text = format.format(date)
            } else {
                textView.text = ""
            }
        }
    }

    fun updateList(newList: List<Task>) {
        list.clear()

        val sorted = newList.sortedBy { it.startAt }

        val groupedList = mutableListOf<Task>()
        var lastDate: String? = null
        val sdf = SimpleDateFormat("yyyyMMdd", Locale.getDefault())

        sorted.forEach { task ->
            val dateStr = task.startAt?.let { sdf.format(it) }
            if (dateStr != lastDate) {
                task.isFirstOfDay = true
                lastDate = dateStr
            } else {
                task.isFirstOfDay = false
            }
            groupedList.add(task)
        }

        list.addAll(groupedList)
        notifyDataSetChanged()
    }
}
