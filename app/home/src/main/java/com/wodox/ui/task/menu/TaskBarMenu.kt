package com.wodox.ui.task.menu

import android.content.Context
import com.wodox.core.data.model.Selectable
import com.wodox.resources.R

data class TaskBarMenu(
    val type: TaskBarMenuType,
    val title: String,
    val icon: Int,
    val selectedIcon: Int,
    override var isSelected: Boolean = false
) : Selectable {
    enum class TaskBarMenuType(val route: String) {
        DETAIL("detail"),
        ACTIVITY("activity");
    }

    companion object {
        fun getDefaults(context: Context): List<TaskBarMenu> {
            return listOf(
                TaskBarMenu(
                    type = TaskBarMenuType.DETAIL,
                    title = context.getString(R.string.details),
                    icon = R.drawable.ic_bottom_bar_home,
                    selectedIcon = R.drawable.ic_bottom_bar_home_selected
                ),
                TaskBarMenu(
                    type = TaskBarMenuType.ACTIVITY,
                    title = context.getString(R.string.activity),
                    icon = R.drawable.ic_bottom_bar_lesson,
                    selectedIcon = R.drawable.ic_bottom_bar_lesson_selected
                ),
            )
        }
    }
}