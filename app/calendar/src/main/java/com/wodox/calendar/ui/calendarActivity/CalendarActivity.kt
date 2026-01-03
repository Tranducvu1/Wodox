package com.wodox.calendar.ui.calendarActivity

import androidx.recyclerview.widget.LinearLayoutManager
import com.wodox.calendar.R
import com.wodox.calendar.databinding.ActivityCalendarBinding
import com.wodox.core.base.activity.BaseActivity
import com.wodox.core.extension.debounceClick
import com.wodox.core.extension.launchWhenStarted
import com.wodox.domain.home.model.local.Task
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class CalendarActivity : BaseActivity<ActivityCalendarBinding, CalendarActivityViewModel>(
    CalendarActivityViewModel::class
) {
    private val timelineAdapter by lazy {
        TimelineAdapter(
            context = this,
            onTaskClick = { task ->

            },
            onStatusChanged = { task, newStatus ->
                viewModel.dispatch(CalendarActivityAction.UpdateTaskStatus(task, newStatus))
            }
        )
    }

    override fun layoutId(): Int = R.layout.activity_calendar

    override fun initialize() {
        setupUI()
        setupRecyclerView()
        setupActions()
        observe()
    }

    private fun setupUI() {
        binding.apply {
            lifecycleOwner = this@CalendarActivity
            viewModel = this@CalendarActivity.viewModel

            val dateFormat = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale.getDefault())
            tvCurrentDate.text = dateFormat.format(Date())
        }
    }

    private fun setupRecyclerView() {
        binding.rvTimeline.apply {
            layoutManager = LinearLayoutManager(this@CalendarActivity)
            adapter = timelineAdapter

            post {
                val currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
                scrollToPosition(currentHour)
            }
        }
    }

    private fun setupActions() {
        binding.apply {
            btnBack.debounceClick {
                finish()
            }

            btnToday.debounceClick {
                val today = Calendar.getInstance().timeInMillis
                this@CalendarActivity.viewModel.dispatch(CalendarActivityAction.SelectDate(today))
            }

            btnPreviousDay.debounceClick {
                this@CalendarActivity.viewModel.dispatch(CalendarActivityAction.PreviousDay)
            }

            btnNextDay.debounceClick {
                this@CalendarActivity.viewModel.dispatch(CalendarActivityAction.NextDay)
            }
        }
    }

    private fun observe() {
        launchWhenStarted {
            viewModel.uiState.collect { state ->
                updateDateDisplay(state.selectedDate)
                updateTimeline(state.tasksOfSelectedDate)
            }
        }
    }

    private fun updateDateDisplay(dateMillis: Long) {
        val date = Date(dateMillis)
        val dateFormat = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale.getDefault())
        binding.tvCurrentDate.text = dateFormat.format(date)
    }

    private fun updateTimeline(tasks: List<Task>) {
        val timeSlots = generateTimeSlots(tasks)
        timelineAdapter.submitList(timeSlots)
    }

    private fun generateTimeSlots(tasks: List<Task>): List<TimeSlot> {
        val slots = mutableListOf<TimeSlot>()
        val calendar = Calendar.getInstance()

        for (hour in 0..23) {
            calendar.set(Calendar.HOUR_OF_DAY, hour)
            calendar.set(Calendar.MINUTE, 0)

            val tasksInHour = tasks.filter { task ->
                task.startAt?.let {
                    val taskCal = Calendar.getInstance().apply { time = it }
                    taskCal.get(Calendar.HOUR_OF_DAY) == hour
                } ?: false
            }
            slots.add(TimeSlot(hour, tasksInHour))
        }

        return slots
    }
}

data class TimeSlot(
    val hour: Int,
    val tasks: List<Task>
)