package com.wodox.data.common.datasource

import android.content.Context
import com.wodox.core.extension.get
import com.wodox.core.extension.set
import com.wodox.config.model.Config
import com.wodox.core.data.model.DatePattern
import com.wodox.core.data.util.GsonUtils
import com.wodox.core.extension.format
import java.util.Date

val Context.usageConfig: UsageConfig get() = UsageConfigImpl.newInstance(applicationContext)

class UsageConfigImpl(val context: Context) : UsageConfig {

    private val sharedPrefs = context.getSharedPreferences(
        Keys.SHARED_PREFS_NAME, Context.MODE_PRIVATE
    )
    private val gson = GsonUtils.provideGson()

    companion object {
        private var shared: UsageConfig? = null

        fun newInstance(context: Context): UsageConfig {
            synchronized(this) {
                if (shared == null) {

                    shared = UsageConfigImpl(context)
                }
                return shared!!
            }
        }

        object Keys {
            const val SHARED_PREFS_NAME: String = "UsageConfig"
            const val CHAT_MESSAGE_DAILY: String = "CHAT_MESSAGE_DAILY"
            const val CHAT_MODEL_USAGE: String = "CHAT_MODEL_USAGE"
            const val ADVANCE_TOOL_USAGE = "ADVANCE_TOOL_USAGE"
            const val IS_USER_RATED = "IS_USER_RATED"
            const val TOTAL_CHAT_MESSAGE = "TOTAL_CHAT_MESSAGE"
            const val APP_OPEN_SESSION = "APP_OPEN_SESSION"
            const val REQUEST_TIMES = "REQUEST_TIMES"
            const val CLICK_PREMIUM_IN_NAV_TIMES = "CLICK_PREMIUM_IN_NAV_TIMES"
            const val TIME_OPEN_APP = "TIME_OPEN_APP"
            const val RESPONSE_TIMES = "RESPONSE_TIMES"
        }

    }

    override var totalChatMessage: Int
        get() = sharedPrefs.get(Keys.TOTAL_CHAT_MESSAGE)
        set(value) {
            sharedPrefs.set(Keys.TOTAL_CHAT_MESSAGE, value)
        }
    override var responseTimes: Int
        get() = sharedPrefs.get(Keys.RESPONSE_TIMES)
        set(value) {
            if (value > 1_000_000) {
                return
            }
            sharedPrefs.set(Keys.RESPONSE_TIMES, value)
        }

    override var chatMessageDaily: ChatMessageDaily
        get() {
            val value = sharedPrefs.getString(Keys.CHAT_MESSAGE_DAILY, "")
            val daily = gson.fromJson(value, ChatMessageDaily::class.java)

            if (daily?.date != Date().format(DatePattern.YYYY_DD_MM)) {
                return ChatMessageDaily(
                    date = Date().format(DatePattern.YYYY_DD_MM),
                    times = 0,
                    availableMessages = Config.ChatMessage.TOTAL_CHAT_MESSAGE_PER_DAY
                )
            }

            return daily
        }
        set(value) {
            sharedPrefs.set(Keys.CHAT_MESSAGE_DAILY, gson.toJson(value))
        }
    override var chatModelUsage: ChatModelUsage
        get() {
            val value = sharedPrefs.getString(Keys.CHAT_MODEL_USAGE, "")

            val usage = gson.fromJson(value, ChatModelUsage::class.java)

            return usage ?: ChatModelUsage()
        }
        set(value) {
            sharedPrefs.set(Keys.CHAT_MODEL_USAGE, gson.toJson(value))
        }
    override var advanceToolUsage: AdvanceToolUsage
        get() {
            val value = sharedPrefs.getString(Keys.ADVANCE_TOOL_USAGE, "")

            val usage = gson.fromJson(value, AdvanceToolUsage::class.java)

            return usage ?: AdvanceToolUsage()
        }
        set(value) {
            sharedPrefs.set(Keys.ADVANCE_TOOL_USAGE, gson.toJson(value))
        }

    override var requestTimes: Int
        get() = sharedPrefs.get(Keys.REQUEST_TIMES)
        set(value) {
            if (value > 1_000_000) {
                return
            }
            sharedPrefs.set(Keys.REQUEST_TIMES, value)
        }
    override var isUserRated: Boolean
        get() = sharedPrefs.get(Keys.IS_USER_RATED)
        set(value) {
            sharedPrefs.set(Keys.IS_USER_RATED, value)
        }

    override var appOpenSession: Int
        get() = sharedPrefs.get(Keys.APP_OPEN_SESSION, 0)
        set(value) {
            sharedPrefs.set(Keys.APP_OPEN_SESSION, value)
        }
    override var clickPremiumInNavTimes: Int
        get() = sharedPrefs.get(Keys.CLICK_PREMIUM_IN_NAV_TIMES)
        set(value) {
            if (value > 1_000_000) {
                return
            }
            sharedPrefs.set(Keys.CLICK_PREMIUM_IN_NAV_TIMES, value)
        }

    override var timeOpenApp: Int
        get() = sharedPrefs.get(Keys.TIME_OPEN_APP, 0)
        set(value) {
            sharedPrefs.set(Keys.TIME_OPEN_APP, value)
        }


}