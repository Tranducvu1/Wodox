package com.wodox.ui.task

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.wodox.domain.home.model.local.Task
import com.wodox.ui.task.activity.TaskActivityFragment
import com.wodox.ui.task.taskdetail.TaskDetailFragment
import com.wodox.ui.task.taskdetail.activitytask.ActivityTaskFragment

class TaskPagerAdapter(
    fragmentActivity: FragmentActivity,
    private val task: Task?
) : FragmentStateAdapter(fragmentActivity) {
    private var pages = ArrayList<Fragment>()

    init {
        pages.add(TaskDetailFragment.newInstance(task))
        pages.add(ActivityTaskFragment.newInstance(task))
    }

    override fun getItemCount(): Int {
        return pages.size
    }

    override fun createFragment(position: Int): Fragment {
        return pages[position]
    }

    fun getFragment(position:Int) : Fragment = pages[position]
}
