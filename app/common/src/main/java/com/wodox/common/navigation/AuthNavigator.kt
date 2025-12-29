package com.wodox.common.navigation

import android.content.Context
import androidx.fragment.app.FragmentManager

interface AuthNavigator {
    fun showWelcome(context: Context)

    fun showSignIn(context: Context, isShowSignUp : Boolean)

    fun showWelcomeClear(context: Context)


}