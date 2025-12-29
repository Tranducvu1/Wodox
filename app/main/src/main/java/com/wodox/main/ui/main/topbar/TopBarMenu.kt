package com.wodox.main.ui.main.topbar

import android.content.Context
import com.wodox.core.data.model.Selectable
import com.wodox.resources.R

data class TopBarMenu(
    val type: TopBarMenuType,
    val title: String,
    val icon: Int,
    val selectedIcon: Int,
    override var isSelected: Boolean = false
) : Selectable {

    enum class TopBarMenuType(val route: String) {
        RECENT("recent"),
        FAVOURITE("favourite"),
        CALENDER("calender"),
        DOCS("docs"),
        MY_WORK("my_work");

        companion object {
            fun fromRoute(route: String?): TopBarMenuType? {
                return entries.find { it.route == route }
            }
        }
    }

    companion object {
        fun getDefaults(context: Context): List<TopBarMenu> {
            return listOf(
                TopBarMenu(
                    type = TopBarMenuType.RECENT,
                    title = context.getString(R.string.recent),
                    icon = R.drawable.ic_home,
                    selectedIcon = R.drawable.ic_home
                ),
                TopBarMenu(
                    type = TopBarMenuType.FAVOURITE,
                    title = context.getString(R.string.my_favourite),
                    icon = R.drawable.ic_favourite,
                    selectedIcon = R.drawable.ic_favourite
                ),
                TopBarMenu(
                    type = TopBarMenuType.CALENDER,
                    title = context.getString(R.string.calender),
                    icon = R.drawable.ic_calendar_home,
                    selectedIcon = R.drawable.ic_calendar_home
                ),
                TopBarMenu(
                    type = TopBarMenuType.DOCS,
                    title = context.getString(R.string.ggdocs),
                    icon = R.drawable.ic_docs,
                    selectedIcon = R.drawable.ic_docs
                ),
            )
        }
    }
}
