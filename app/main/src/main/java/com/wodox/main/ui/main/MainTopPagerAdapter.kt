package com.wodox.main.ui.main

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.wodox.calendar.ui.CalendarFragment
import com.wodox.docs.ui.docdetail.DocsDetailActivity
import com.wodox.docs.ui.docs.DocsFragment
import com.wodox.ui.home.HomeFragment
import com.wodox.ui.favourite.FavouriteFragment

class MainTopPagerAdapter(
    val fragmentActivity: FragmentActivity,
) : FragmentStateAdapter(fragmentActivity) {
    private var pages = ArrayList<Fragment>()

    init {
        pages.add(HomeFragment.newInstance())
        pages.add(FavouriteFragment.newInstance())
        pages.add(CalendarFragment.newInstance())
        pages.add(DocsFragment.newInstance())
    }

    override fun getItemCount(): Int {
        return pages.size
    }

    override fun createFragment(position: Int): Fragment {
        return pages[position]
    }

}

