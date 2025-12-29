package com.wodox.intro.ui.intro.tab

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.wodox.intro.model.IntroData

class IntroPagerAdapter(
    activity: FragmentActivity,
    private val intros: List<IntroData>
) : FragmentStateAdapter(activity) {

    override fun getItemCount(): Int = intros.size

    override fun createFragment(position: Int): Fragment {
        return IntroFragment.newInstance(intros[position])
    }
}
