package com.wodox.util

import android.content.Context
import com.wodox.common.ui.menuview.MenuOption
import com.wodox.common.ui.menuview.MenuOptionType
import com.wodox.resources.R

class TaskMenuUtil {
    companion object {
        fun getItemMenus(
            context: Context,
        ): ArrayList<MenuOption> {
            val menus = ArrayList<MenuOption>()
            menus.add(
                MenuOption(
                    type = MenuOptionType.TODO,
                    nameResId = R.string.todo,
                    iconResId = R.drawable.ic_favorite_unselection,
                )
            )

            menus.add(
                MenuOption(
                    type = MenuOptionType.IN_PROGRESS,
                    nameResId = R.string.inprogress,
                    iconResId = R.drawable.ic_drawing_menu_edit
                )
            )

            menus.add(
                MenuOption(
                    type = MenuOptionType.COMPLETE,
                    nameResId = R.string.complete,
                    iconResId = R.drawable.ic_delete_draw,
                    tintColor = context.getColor(R.color.colorDelete)
                )
            )
            return menus
        }

        fun getItemMenusPriority(
            context: Context,
        ): ArrayList<MenuOption> {
            val menus = ArrayList<MenuOption>()

            menus.add(
                MenuOption(
                    type = MenuOptionType.LOW,
                    nameResId = R.string.low,
                    iconResId = com.wodox.home.R.drawable.ic_priority_low,
                    tintColor = context.getColor(R.color.priority_low)
                )
            )

            menus.add(
                MenuOption(
                    type = MenuOptionType.NORMAL,
                    nameResId = R.string.normal,
                    iconResId = com.wodox.home.R.drawable.ic_priority_normal,
                    tintColor = context.getColor(R.color.priority_normal)
                )
            )

            menus.add(
                MenuOption(
                    type = MenuOptionType.HIGH,
                    nameResId = R.string.high,
                    iconResId = com.wodox.home.R.drawable.ic_priority_high,
                    tintColor = context.getColor(R.color.priority_high)
                )
            )

            return menus
        }

        fun getItemMenusDifficulty(
            context: Context,
        ): ArrayList<MenuOption> {
            val menus = ArrayList<MenuOption>()

            menus.add(
                MenuOption(
                    type = MenuOptionType.VERY_EASY,
                    nameResId = R.string.very_easy,
                    iconResId = com.wodox.home.R.drawable.ic_flag,
                    tintColor = context.getColor(R.color.difficulty_very_easy)
                )
            )

            menus.add(
                MenuOption(
                    type = MenuOptionType.EASY,
                    nameResId = R.string.easy,
                    iconResId = com.wodox.home.R.drawable.ic_flag,
                    tintColor = context.getColor(R.color.difficulty_easy)
                )
            )

            menus.add(
                MenuOption(
                    type = MenuOptionType.NORMAL,
                    nameResId = R.string.normal,
                    iconResId = com.wodox.home.R.drawable.ic_flag,
                    tintColor = context.getColor(R.color.colorDelete)
                )
            )

            menus.add(
                MenuOption(
                    type = MenuOptionType.HARD,
                    nameResId = R.string.hard,
                    iconResId = com.wodox.home.R.drawable.ic_flag,
                    tintColor = context.getColor(R.color.difficulty_hard)
                )
            )

            menus.add(
                MenuOption(
                    type = MenuOptionType.VERY_HARD,
                    nameResId = R.string.very_hard,
                    iconResId = com.wodox.home.R.drawable.ic_flag,
                    tintColor = context.getColor(R.color.difficulty_very_hard)
                )
            )

            return menus
        }
    }
}