package com.wodox.calendar.ui

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.CalendarMonth
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.daysOfWeek
import com.kizitonwose.calendar.view.MonthDayBinder
import com.kizitonwose.calendar.view.MonthHeaderFooterBinder
import com.wodox.core.extension.launchWhenStarted
import com.wodox.calendar.R
import com.wodox.calendar.databinding.CalendarDayBinding
import com.wodox.calendar.databinding.FragmentCalendarLayoutBinding
import com.wodox.calendar.databinding.ItemCalenderMonthBinding
import com.wodox.core.base.fragment.BaseFragment
import com.wodox.domain.home.model.local.Task
import com.wodox.domain.home.model.local.TaskStatus
import dagger.hilt.android.AndroidEntryPoint
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import java.time.format.TextStyle
import java.util.Calendar
import java.util.Locale

@AndroidEntryPoint
class CalendarFragment : BaseFragment<FragmentCalendarLayoutBinding, CalenderViewModel>(
    CalenderViewModel::class
) {
    private var selectedDate: LocalDate = LocalDate.now()
    private var allTasks: List<Task> = emptyList()

    override fun layoutId(): Int = R.layout.fragment_calendar_layout

    override fun initialize() {
        setupUI()
        setupCalendar()
        observe()
    }

    private fun setupUI() {
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        setupRecyclerView()
    }

    private val calendarAdapter by lazy {
        TaskCalendarAdapter(
            requireContext(),
            object : TaskCalendarAdapter.OnItemClickListener {
                override fun onStatusChanged(task: Task, newStatus: TaskStatus) {
                    viewModel.dispatch(
                        CalenderUiAction.UpdateTaskStatus(task, newStatus)
                    )
                }
            }
        )
    }

    private fun setupRecyclerView() {
        binding.rvTask.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = calendarAdapter
        }
    }

    private fun setupCalendar() {
        val daysOfWeek = daysOfWeek()
        setupDaysOfWeekHeader(daysOfWeek)
        val currentMonth = YearMonth.now()
        val startMonth = currentMonth.minusMonths(12)
        val endMonth = currentMonth.plusMonths(12)

        binding.calendarView.apply {
            setup(startMonth, endMonth, daysOfWeek.first())
            scrollToMonth(currentMonth)

            monthScrollListener = { month ->
                val monthName =
                    month.yearMonth.month.getDisplayName(TextStyle.FULL, Locale.getDefault())
                binding.tvMonthTitle.text =
                    "${monthName.replaceFirstChar { it.uppercase() }} ${month.yearMonth.year}"
            }

            monthHeaderBinder = object : MonthHeaderFooterBinder<MonthViewHolder> {
                override fun create(view: View): MonthViewHolder {
                    val binding = ItemCalenderMonthBinding.inflate(
                        LayoutInflater.from(requireContext()),
                        null,
                        false
                    )
                    return MonthViewHolder(binding)
                }

                override fun bind(container: MonthViewHolder, data: CalendarMonth) {
                    container.bind(data)
                }
            }

            dayBinder = object : MonthDayBinder<DayViewHolder> {
                override fun create(view: View): DayViewHolder {
                    val binding = CalendarDayBinding.bind(view)
                    return DayViewHolder(binding) { day ->
                        if (day.position == DayPosition.MonthDate) {
                            selectDate(day.date)
                        }
                    }
                }

                override fun bind(container: DayViewHolder, data: CalendarDay) {
                    val tasksForDay = getTasksForDate(data.date)
                    val isSelected = data.date == selectedDate
                    container.bind(data, tasksForDay, isSelected)

                    if (data.position != DayPosition.MonthDate) {
                        container.binding.tvDayNumber.alpha = 0.3f
                    } else {
                        container.binding.tvDayNumber.alpha = 1f
                    }
                }
            }
        }
    }

    private fun setupDaysOfWeekHeader(daysOfWeek: List<DayOfWeek>) {
        binding.llDaysOfWeekHeader.removeAllViews()

        daysOfWeek.forEach { dayOfWeek ->
            val textView = LayoutInflater.from(requireContext())
                .inflate(
                    R.layout.calendar_day_header,
                    binding.llDaysOfWeekHeader,
                    false
                ) as TextView

            val dayName = when (dayOfWeek) {
                DayOfWeek.SUNDAY -> "CN"
                DayOfWeek.MONDAY -> "T2"
                DayOfWeek.TUESDAY -> "T3"
                DayOfWeek.WEDNESDAY -> "T4"
                DayOfWeek.THURSDAY -> "T5"
                DayOfWeek.FRIDAY -> "T6"
                DayOfWeek.SATURDAY -> "T7"
            }
            textView.text = dayName
            binding.llDaysOfWeekHeader.addView(textView)
        }
    }

    private fun selectDate(date: LocalDate) {
        if (selectedDate != date) {
            val oldDate = selectedDate
            selectedDate = date
            binding.calendarView.notifyDateChanged(date)
            binding.calendarView.notifyDateChanged(oldDate)
            val millis = date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
            viewModel.dispatch(CalenderUiAction.UpdateSelectedDate(millis))
        }
    }

    private fun getTasksForDate(date: LocalDate): List<Task> {
        return allTasks.filter { task ->
            task.startAt?.let {
                val taskCalendar = Calendar.getInstance().apply { time = it }
                taskCalendar.get(Calendar.YEAR) == date.year &&
                        taskCalendar.get(Calendar.MONTH) == date.monthValue - 1 &&
                        taskCalendar.get(Calendar.DAY_OF_MONTH) == date.dayOfMonth
            } ?: false
        }.sortedBy { it.startAt }
    }

    private fun observe() {
        // Collect allTasks từ ViewModel
        launchWhenStarted {
            viewModel.allTasks.collect { tasks ->
                Log.d("CalendarFragment", "All tasks: $tasks")
                allTasks = tasks
                updateTasksForSelectedDate()
                binding.calendarView.notifyCalendarChanged()
            }
        }

        // Collect uiState để cập nhật adapter khi selectedDate thay đổi
        launchWhenStarted {
            viewModel.uiState.collect { state ->
                updateTasksForSelectedDate(state.selectedDate)
            }
        }
    }

    // Hàm helper để lọc tasks theo selectedDate và cập nhật adapter
    private fun updateTasksForSelectedDate(selectedDateMillis: Long? = null) {
        val dateMillis = selectedDateMillis ?: this@CalendarFragment.selectedDate
            .atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()

        val tasksForDate = allTasks.filter { task ->
            task.startAt?.time?.let {
                val taskCalendar = Calendar.getInstance().apply { timeInMillis = it }
                val selectedCalendar = Calendar.getInstance().apply { timeInMillis = dateMillis }
                taskCalendar.get(Calendar.YEAR) == selectedCalendar.get(Calendar.YEAR) &&
                        taskCalendar.get(Calendar.MONTH) == selectedCalendar.get(Calendar.MONTH) &&
                        taskCalendar.get(Calendar.DAY_OF_MONTH) == selectedCalendar.get(Calendar.DAY_OF_MONTH)
            } ?: false
        }.sortedBy { it.startAt }

        calendarAdapter.submitList(tasksForDate)
    }


    companion object {
        fun newInstance() = CalendarFragment()
    }
}