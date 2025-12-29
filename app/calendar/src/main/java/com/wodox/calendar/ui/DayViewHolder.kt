package com.wodox.calendar.ui

import android.view.LayoutInflater
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.view.ViewContainer
import com.wodox.calendar.databinding.CalendarDayBinding
import com.wodox.calendar.databinding.ItemCalendarTaskBinding
import com.wodox.domain.home.model.local.Task
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.UUID

class DayViewHolder(
    val binding: CalendarDayBinding,
    private val onDayClick: (CalendarDay) -> Unit
) : ViewContainer(binding.root) {

    private lateinit var day: CalendarDay

    init {
        binding.root.setOnClickListener {
            if (::day.isInitialized) {
                onDayClick(day)
            }
        }
    }

    fun bind(calendarDay: CalendarDay, tasks: List<Task>, isSelected: Boolean) {
        this.day = calendarDay

        binding.day = calendarDay
        binding.isSelected = isSelected
        binding.executePendingBindings()

        binding.llTasksList.removeAllViews()

        val maxVisibleTasks = 2
        val tasksToShow = tasks.take(maxVisibleTasks)
        val inflater = LayoutInflater.from(binding.root.context)
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

        tasksToShow.forEach { task ->
            val taskBinding = ItemCalendarTaskBinding.inflate(inflater, binding.llTasksList, false)
            taskBinding.task = task
            taskBinding.executePendingBindings()
            binding.llTasksList.addView(taskBinding.root)
        }

        if (tasks.size > maxVisibleTasks) {
            val moreBinding = ItemCalendarTaskBinding.inflate(inflater, binding.llTasksList, false)
            moreBinding.task = Task(title = "+${tasks.size - maxVisibleTasks} more",ownerId = UUID.randomUUID()
            )
            moreBinding.executePendingBindings()
            binding.llTasksList.addView(moreBinding.root)
        }
    }
}
