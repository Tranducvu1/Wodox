package com.wodox.auth.navigation

import android.content.Context
import android.content.Intent
import com.wodox.auth.ui.sigin.SignInActivity
import com.wodox.auth.ui.welcome.WelcomeActivity
import com.wodox.common.navigation.AuthNavigator
import com.wodox.core.extension.openActivity
import com.wodox.core.extension.openActivityWithFlags


class AuthNavigatorImpl : AuthNavigator {
    override fun showWelcome(
        context: Context
    ) {
        context.openActivity<WelcomeActivity>()
    }

    override fun showSignIn(context: Context, isShowSignUp: Boolean) {
        context.openActivity<SignInActivity>(
            com.wodox.auth.model.Constants.Intents.IS_SHOW_SIGN_UP to isShowSignUp
        )
    }

    override fun showWelcomeClear(context: Context) {
        context.openActivityWithFlags<WelcomeActivity>(
            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        )
    }

}