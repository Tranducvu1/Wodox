package com.wodox.intro.navigation

import android.content.Context
import com.wodox.common.navigation.IntroNavigator
import com.wodox.core.extension.openActivity
import com.wodox.intro.ui.splash.SplashActivity

class IntroNavigatorImpl: IntroNavigator {
    override fun openSplash(context: Context, isMoveToForeground: Boolean) {
        return context.openActivity<SplashActivity>(
        )
    }
}