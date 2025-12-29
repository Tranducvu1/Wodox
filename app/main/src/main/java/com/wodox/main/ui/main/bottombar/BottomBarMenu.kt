package com.wodox.main.ui.main.bottombar

import android.content.Context
import com.wodox.core.data.model.Selectable
import com.wodox.main.ui.main.PageType
import com.wodox.main.ui.main.topbar.TopBarMenu.TopBarMenuType
import com.wodox.resources.R

data class BottomBarMenu(
    var type: BottomBarMenuType,
    var title: String,
    var icon: Int,
    var selectedIcon: Int,
    override var isSelected: Boolean = false
) : Selectable {
    enum class BottomBarMenuType(val page: PageType, val index: Int) {
        HOME(PageType.HOME, 0),
        ACTIVITY(PageType.ACTIVITY, 1),
        CREATE(PageType.CREATE, 2),
        MY_WORK(PageType.MY_WORK, 3);
    }

    companion object {
        fun getDefaults(context: Context): List<BottomBarMenu> {
            return arrayListOf(
                BottomBarMenu(
                    BottomBarMenuType.HOME,
                    context.getString(R.string.home),
                    R.drawable.ic_home,
                    R.drawable.ic_home
                ),
                BottomBarMenu(
                    BottomBarMenuType.ACTIVITY,
                    context.getString(R.string.chat),
                    R.drawable.ic_activity_home,
                    R.drawable.ic_activity_home
                ),
                BottomBarMenu(
                    BottomBarMenuType.CREATE,
                    context.getString(R.string.create),
                    R.drawable.ic_create,
                    R.drawable.ic_create
                ),
                BottomBarMenu(
                    type = BottomBarMenuType.MY_WORK,
                    title = context.getString(R.string.my_work),
                    icon = R.drawable.ic_my_work,
                    selectedIcon = R.drawable.ic_my_work
                ),
            )
        }
    }
}