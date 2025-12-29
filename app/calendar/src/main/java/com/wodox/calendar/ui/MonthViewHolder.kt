package com.wodox.calendar.ui


import com.kizitonwose.calendar.core.CalendarMonth
import com.kizitonwose.calendar.view.ViewContainer
import com.wodox.calendar.databinding.ItemCalenderMonthBinding
import java.time.format.TextStyle
import java.util.Locale

class MonthViewHolder(
    private val binding: ItemCalenderMonthBinding
) : ViewContainer(binding.root) {

    fun bind(calendarMonth: CalendarMonth) {
        val monthName = calendarMonth.yearMonth.month.getDisplayName(
            TextStyle.FULL,
            Locale.getDefault()
        )
        val year = calendarMonth.yearMonth.year
        binding.monthText = "$monthName $year"
        binding.executePendingBindings()
    }
}
