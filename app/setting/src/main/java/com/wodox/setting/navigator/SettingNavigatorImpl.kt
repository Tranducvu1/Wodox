package com.wodox.setting.navigator

import android.content.Context
import com.wodox.common.navigation.SettingNavigator

class SettingNavigatorImpl : SettingNavigator {

    override fun openNotifications(context: Context) {
        // Navigate to notifications screen
        // Example: context.startActivity(Intent(context, NotificationActivity::class.java))
    }

    override fun openChangePassword(context: Context) {
        // Navigate to change password screen
        // Example: context.startActivity(Intent(context, ChangePasswordActivity::class.java))
    }

    override fun openLanguage(context: Context) {
        // Navigate to language selection screen
        // Example: context.startActivity(Intent(context, LanguageActivity::class.java))
    }

    override fun openPrivacyPolicy(context: Context) {
        // Navigate to privacy policy screen
        // Example: context.startActivity(Intent(context, PrivacyPolicyActivity::class.java))
    }
}
