package com.wodox.ui.home

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.wodox.core.extension.debounceClick
import com.wodox.common.util.HtmlConverterUtil
import com.wodox.core.ui.adapter.TMVVMViewHolder
import com.wodox.domain.home.model.local.Task
import com.wodox.home.BR
import com.wodox.home.databinding.ItemTaskLayoutBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class TaskAdapter(private val context: Context?, private val listener: OnItemClickListener) :
    PagingDataAdapter<Task, TMVVMViewHolder>(DIFF_CALLBACK) {
    interface OnItemClickListener {
        fun onClick(task: Task)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TMVVMViewHolder {
        val binding =
            ItemTaskLayoutBinding.inflate(LayoutInflater.from(parent?.context), parent, false)
        return TMVVMViewHolder(binding)
    }
    override fun onBindViewHolder(
        holder: TMVVMViewHolder,
        position: Int
    ) {
        val task = getItem(position) ?: return
        val binding = holder.binding as ItemTaskLayoutBinding
        binding.llContainer.debounceClick {
            listener.onClick(task)
        }
        val html = task.description.orEmpty()
        val spanned = HtmlConverterUtil.convertToSpannedSync(html)
        binding.tvDescription.setText(spanned, TextView.BufferType.SPANNABLE)
        binding.setVariable(BR.task, task)
        binding.executePendingBindings()
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Task>() {
            override fun areItemsTheSame(oldItem: Task, newItem: Task) =
                oldItem == newItem

            override fun areContentsTheSame(oldItem: Task, newItem: Task) =
                areItemsTheSame(oldItem, newItem)
        }

        @JvmStatic
        @BindingAdapter("app:formatTaskDated")
        fun setFormattedDate(textView: TextView, date: Date?) {
            if (date != null) {
                val calendar = Calendar.getInstance()
                val currentYear = calendar.get(Calendar.YEAR)

                val dateCalender = Calendar.getInstance()
                dateCalender.time = date
                val yearOfDate = dateCalender.get(Calendar.YEAR)

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
}