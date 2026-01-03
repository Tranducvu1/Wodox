package com.wodox.calendar.ui.calendarActivity

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.wodox.calendar.databinding.ItemTimeLineTaskBinding
import com.wodox.calendar.databinding.ItemTimelineHourBinding
import com.wodox.domain.home.model.local.Task
import com.wodox.domain.home.model.local.TaskStatus
import java.text.SimpleDateFormat
import java.util.*

class TimelineAdapter(
    private val context: Context,
    private val onTaskClick: (Task) -> Unit,
    private val onStatusChanged: (Task, TaskStatus) -> Unit
) : ListAdapter<TimeSlot, TimelineAdapter.TimelineViewHolder>(TimeSlotDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimelineViewHolder {
        val binding = ItemTimelineHourBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TimelineViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TimelineViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class TimelineViewHolder(
        private val binding: ItemTimelineHourBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(timeSlot: TimeSlot) {
            binding.apply {
                // Display hour
                tvHour.text = formatHour(timeSlot.hour)

                val currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
                val isCurrentHour = timeSlot.hour == currentHour

                if (isCurrentHour) {
                    tvHour.setTextColor(ContextCompat.getColor(context, com.wodox.resources.R.color.primary))
                    viewTimeline.setBackgroundColor(ContextCompat.getColor(context, com.wodox.resources.R.color.primary))
                } else {
                    tvHour.setTextColor(ContextCompat.getColor(context, com.wodox.resources.R.color.text_secondary))
                    viewTimeline.setBackgroundColor(ContextCompat.getColor(context, com.wodox.resources.R.color.color78D7FF))
                }

                // Clear previous tasks
                llTasks.removeAllViews()

                // Add tasks for this hour
                timeSlot.tasks.forEach { task ->
                    val taskView = createTaskView(task)
                    llTasks.addView(taskView)
                }
            }
        }

        private fun createTaskView(task: Task): android.view.View {
            val taskBinding = ItemTimeLineTaskBinding.inflate(
                LayoutInflater.from(context),
                binding.llTasks,
                false
            )

            taskBinding.apply {
                tvTaskTitle.text = task.title
                tvTaskTime.text = formatTaskTime(task)

                val color = when (task.status) {
                    TaskStatus.DONE -> com.wodox.resources.R.color.support_none
                    TaskStatus.IN_PROGRESS -> com.wodox.resources.R.color.support_medium
                    TaskStatus.TODO -> com.wodox.resources.R.color.difficulty_very_easy
                    TaskStatus.BLOCKED -> com.wodox.resources.R.color.error
                }
                viewTaskIndicator.setBackgroundColor(ContextCompat.getColor(context, color))

                root.setOnClickListener { onTaskClick(task) }

                ivStatus.setOnClickListener {
                    showStatusMenu(task)
                }
            }

            return taskBinding.root
        }

        private fun formatHour(hour: Int): String {
            val calendar = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, hour)
                set(Calendar.MINUTE, 0)
            }
            val format = SimpleDateFormat("HH:mm", Locale.getDefault())
            return format.format(calendar.time)
        }

        private fun formatTaskTime(task: Task): String {
            val start = task.startAt ?: return ""
            val end = task.dueAt ?: return ""

            val format = SimpleDateFormat("HH:mm", Locale.getDefault())
            return "${format.format(start)} - ${format.format(end)}"
        }

        private fun showStatusMenu(task: Task) {
            val popup = android.widget.PopupMenu(context, binding.root)
            popup.menu.apply {
                add(0, 0, 0, "To Do")
                add(0, 1, 1, "In Progress")
                add(0, 2, 2, "Completed")
                add(0, 3, 3, "Cancelled")
            }

            popup.setOnMenuItemClickListener { menuItem ->
                val newStatus = when (menuItem.itemId) {
                    0 -> TaskStatus.TODO
                    1 -> TaskStatus.IN_PROGRESS
                    2 -> TaskStatus.DONE
                    3 -> TaskStatus.BLOCKED
                    else -> return@setOnMenuItemClickListener false
                }
                onStatusChanged(task, newStatus)
                true
            }

            popup.show()
        }
    }
}

class TimeSlotDiffCallback : DiffUtil.ItemCallback<TimeSlot>() {
    override fun areItemsTheSame(oldItem: TimeSlot, newItem: TimeSlot): Boolean {
        return oldItem.hour == newItem.hour
    }

    override fun areContentsTheSame(oldItem: TimeSlot, newItem: TimeSlot): Boolean {
        return oldItem == newItem
    }
}