package com.wodox.common.navigation

import android.content.Context
import androidx.fragment.app.FragmentManager
import com.wodox.domain.home.model.local.SubTask
import com.wodox.domain.home.model.local.Task

interface HomeNavigator {
    fun openTask(context: Context,task: Task)

    fun openDescription(context: Context,task:Task)

    fun openSubTask(fragmentManager: FragmentManager, task: Task?,subTask: SubTask?)

    fun openCheckList(context: Context, task: Task?)

    fun openCreateTask(fragmentManager: FragmentManager)

    fun openAIBottomSheet(fragmentManager: FragmentManager, task: Task?)

    fun openMenuBottomSheet(fragmentManager: FragmentManager, task: Task?)

}