package com.wodox.common.navigation

import android.content.Context


interface MainNavigator {
    fun showMain(context: Context, isFirstLaunch: Boolean)

    fun showAddPerson(fragmentManager: androidx.fragment.app.FragmentManager)

    fun showProfile(fragmentManager: androidx.fragment.app.FragmentManager)


}