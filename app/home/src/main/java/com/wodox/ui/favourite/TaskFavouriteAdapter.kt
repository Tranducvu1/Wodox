package com.wodox.ui.favourite

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.wodox.core.ui.adapter.TMVVMViewHolder
import com.wodox.domain.home.model.local.Task
import com.wodox.home.BR
import java.text.SimpleDateFormat
import java.util.*
import com.wodox.home.databinding.ItemTaskFavouriteLayoutBinding

class TaskFavouriteAdapter(
    private val context: Context?,
    private val listener: OnItemClickListener
) : PagingDataAdapter<Task, TMVVMViewHolder>(DIFF_CALLBACK) {
    interface OnItemClickListener {
        fun onMenuClick(task: Task)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TMVVMViewHolder {
        val binding =
            ItemTaskFavouriteLayoutBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return TMVVMViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: TMVVMViewHolder,
        position: Int
    ) {
        val task = getItem(position) ?: return
        val binding = holder.binding as ItemTaskFavouriteLayoutBinding

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
        @BindingAdapter("app:formatFavouriteDate")
        fun setFormattedDate(textView: TextView, date: Date?) {
            if (date != null) {
                val format = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                textView.text = format.format(date)
            } else {
                textView.text = ""
            }
        }
    }
}