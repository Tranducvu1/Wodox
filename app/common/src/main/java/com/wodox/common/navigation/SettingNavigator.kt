package com.wodox.common.navigation

import android.content.Context

interface SettingNavigator {
    fun openNotifications(context: Context)
    fun openChangePassword(context: Context)
    fun openLanguage(context: Context)
    fun openPrivacyPolicy(context: Context)
}
