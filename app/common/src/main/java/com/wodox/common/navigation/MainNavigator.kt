package com.wodox.common.navigation

import android.content.Context
import androidx.fragment.app.FragmentManager


interface MainNavigator {
    fun showMain(context: Context, isFirstLaunch: Boolean)

    fun showAddPerson(fragmentManager: androidx.fragment.app.FragmentManager)

    fun showProfile(fragmentManager: androidx.fragment.app.FragmentManager)

    fun openAIBottomSheet(fragmentManager: FragmentManager)

    fun openEditActivity(context: Context)

}