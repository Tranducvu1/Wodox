package com.wodox.navigation

import android.content.Context
import androidx.fragment.app.FragmentManager
import com.wodox.common.navigation.HomeNavigator
import com.wodox.core.extension.openActivity
import com.wodox.core.extension.showAllowingStateLoss
import com.wodox.domain.home.model.local.SubTask
import com.wodox.domain.home.model.local.Task
import com.wodox.model.Constants
import com.wodox.ui.task.TaskActivity
import com.wodox.ui.task.aibottomsheet.AiResponseBottomSheet
import com.wodox.ui.task.menuoption.OptionMenuBottomSheet
import com.wodox.ui.task.taskdetail.checklist.CheckListActivity
import com.wodox.ui.task.taskdetail.createtask.CreateTaskBottomSheet
import com.wodox.ui.task.taskdetail.description.DescriptionActivity
import com.wodox.ui.task.taskdetail.subtask.SubTaskBottomSheet

class HomeNavigatorImpl() : HomeNavigator {
    override fun openTask(context: Context, task: Task) {
        return context.openActivity<TaskActivity>(
            Constants.Intents.TASK to task
        )
    }

    override fun openDescription(context: Context, task: Task) {
        return context.openActivity<DescriptionActivity>(
            Constants.Intents.TASK to task
        )
    }

    override fun openSubTask(fragmentManager: FragmentManager, task: Task?, subTask: SubTask?) {
        SubTaskBottomSheet.newInstance(task, subTask).apply {
            showAllowingStateLoss(fragmentManager, "")
        }
    }

    override fun openCheckList(context: Context, task: Task?) {
        context.openActivity<CheckListActivity>(
            Constants.Intents.TASK to task
        )
    }

    override fun openCreateTask(fragmentManager: FragmentManager) {
        CreateTaskBottomSheet.newInstance().apply {
            showAllowingStateLoss(fragmentManager, "")
        }
    }

    override fun openAIBottomSheet(fragmentManager: FragmentManager, task: Task?) {
        AiResponseBottomSheet.newInstance(task).apply {
            showAllowingStateLoss(fragmentManager, "")
        }
    }

    override fun openMenuBottomSheet(fragmentManager: FragmentManager, task: Task?) {
        OptionMenuBottomSheet.newInstance(task).apply {
            showAllowingStateLoss(fragmentManager, "")
        }
    }
}