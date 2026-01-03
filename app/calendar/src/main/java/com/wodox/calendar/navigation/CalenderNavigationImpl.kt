package com.wodox.calendar.navigation

import android.content.Context
import com.wodox.calendar.ui.calendarActivity.CalendarActivity
import com.wodox.common.navigation.CalendarNavigator
import com.wodox.core.extension.openActivity


class CalenderNavigationImpl : CalendarNavigator {
    override fun openMyCalendar(context: Context) {
        return context.openActivity<CalendarActivity>()
    }
}