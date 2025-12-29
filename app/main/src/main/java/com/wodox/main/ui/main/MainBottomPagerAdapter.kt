package com.wodox.main.ui.main

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.wodox.chat.ui.chat.ChatFragment
import com.wodox.ui.home.HomeFragment
import com.wodox.mywork.ui.MyWorkFragment
import com.wodox.ui.task.optioncreate.OptionCreateFragment

class MainBottomPagerAdapter(
    val fragmentActivity: FragmentActivity,
) : FragmentStateAdapter(fragmentActivity) {
    private var pages = ArrayList<Fragment>()

    init {
        pages.add(HomeFragment.newInstance())
        pages.add(ChatFragment.newInstance())
        pages.add(OptionCreateFragment.newInstance())
        pages.add(MyWorkFragment.newInstance())
    }

    override fun getItemCount(): Int {
        return pages.size
    }

    override fun createFragment(position: Int): Fragment {
        return pages[position]
    }

}
