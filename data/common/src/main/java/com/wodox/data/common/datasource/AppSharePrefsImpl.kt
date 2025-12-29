package com.wodox.data.common.datasource

import android.content.SharedPreferences
import com.google.gson.Gson
import com.osprey.core.extension.get
import com.osprey.core.extension.set
import java.util.Locale
import javax.inject.Inject

class AppSharePrefsImpl @Inject constructor(
    private val sharedPrefs: SharedPreferences,
    private val gson: Gson = Gson()
) :
    AppSharePrefs {

    object Keys {
        const val SHARED_PREFS_NAME = "LearnDraw"
        const val CURRENT_LANGUAGE = "CURRENT_LANGUAGE"
        const val INSTALL_TIME = "INSTALL_TIME"
        const val CURRENT_COUNTRY_CODE = "CURRENT_COUNTRY_CODE"
        const val IS_FIRST_TIME = "IS_FIRST_TIME"
        const val IS_PURCHASED = "IS_PURCHASED"
        const val OPEN_TIMES = "OPEN_TIMES"
        const val DEVICE_ID = "DEVICE_ID"
        const val DOWNLOADED_THEMES = "DOWNLOADED_THEMES"
        const val KEYBOARD_THEME = "KEYBOARD_THEME"
        const val HAD_A_SUCCESSFUL_KEYBOARD_INSTALLATION = "HAD_A_SUCCESSFUL_KEYBOARD_INSTALLATION"
        const val IS_CHAT_SUGGESTION = "IS_CHAT_SUGGESTION"
        const val IS_ENABLE_HOME_SEND_ANIMATION = "IS_ENABLE_HOME_SEND_ANIMATION"
    }

    override var currentCodeLang: String
        get() {
            val format: String = sharedPrefs.get(Keys.CURRENT_LANGUAGE)
            return if (format.isNullOrEmpty()) {
                Locale.getDefault().language
            } else {
                format
            }
        }
        set(value) {
            sharedPrefs.set(Keys.CURRENT_LANGUAGE, value)
        }

    override var currentCountryCode: String
        get() = sharedPrefs.get(Keys.CURRENT_COUNTRY_CODE)
        set(value) {
            sharedPrefs.set(Keys.CURRENT_COUNTRY_CODE, value)
        }

    override var installTime: Long
        get() = sharedPrefs.get(Keys.INSTALL_TIME)
        set(value) {
            sharedPrefs.set(Keys.INSTALL_TIME, value)
        }

    override var isFirstOpen: Boolean
        get() = sharedPrefs.get(Keys.IS_FIRST_TIME, true)
        set(value) {
            sharedPrefs.set(Keys.IS_FIRST_TIME, value)
        }

    override var isPurchased: Boolean
        get() = sharedPrefs.get(Keys.IS_PURCHASED)
        set(value) {
            sharedPrefs.set(Keys.IS_PURCHASED, value)
        }

    override var openTimes: Int
        get() = sharedPrefs.get(Keys.OPEN_TIMES)
        set(value) {
            sharedPrefs.set(Keys.OPEN_TIMES, value)
        }
    override var deviceId: String?
        get() = sharedPrefs.get(Keys.DEVICE_ID)
        set(value) {
            sharedPrefs.set(Keys.DEVICE_ID, value)
        }
    override var hadASuccessfulKeyboardInstallation: Boolean
        get() = sharedPrefs.get(Keys.HAD_A_SUCCESSFUL_KEYBOARD_INSTALLATION, false)
        set(value) {
            sharedPrefs.set(Keys.HAD_A_SUCCESSFUL_KEYBOARD_INSTALLATION, value)
        }
    override var isChatSuggestion: Boolean
        get() = sharedPrefs.get(Keys.IS_CHAT_SUGGESTION, true)
        set(value) {
            sharedPrefs.set(Keys.IS_CHAT_SUGGESTION, value)
        }
    override var isEnableHomeSendAnimation: Boolean
        get() = sharedPrefs.get(Keys.IS_ENABLE_HOME_SEND_ANIMATION, true)
        set(value) {
            sharedPrefs.set(Keys.IS_ENABLE_HOME_SEND_ANIMATION, value)
        }
}