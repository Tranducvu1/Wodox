package com.wodox.data.common.datasource

import android.annotation.SuppressLint
import android.content.Context
import com.wodox.chat.model.AdvanceToolType
import com.wodox.domain.chat.model.ChatModel
import com.wodox.domain.chat.model.UsageKey
import com.starnest.data.common.extension.isPremium
import com.wodox.config.model.Config
import com.wodox.core.app.AbstractApplication
import com.wodox.core.data.model.DatePattern
import com.wodox.core.extension.format
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.Date
import kotlin.math.max

interface UsageConfig {
    var chatMessageDaily: ChatMessageDaily
    var chatModelUsage: ChatModelUsage
    var advanceToolUsage: AdvanceToolUsage
    var responseTimes: Int
    var requestTimes: Int
    var totalChatMessage: Int
    var isUserRated: Boolean
    var appOpenSession: Int
    var clickPremiumInNavTimes: Int
    var timeOpenApp: Int
}


abstract class AppUsage<T : UsageKey> {
    abstract fun getUsage(key: T): Int

    open fun getFreeTimes(key: T): Int {
        return 1
    }

    fun canGenerate(context: Context, key: T): Boolean {
        if (context.isPremium) {
            return true
        }

        val usage = getUsage(key)

        return usage < getFreeTimes(key)
    }
}

@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class ChatMessageDaily(
    @SerialName("date")
    val date: String,
    @SerialName("times")
    var times: Int,
    @SerialName("totalMessages")
    var availableMessages: Int = Config.ChatMessage.TOTAL_CHAT_MESSAGE_PER_DAY,
    @SerialName("adViewed")
    var adViewed: Boolean = false,
    @SerialName("messageRequest")
    var messageRequest: Int = 0,
    @SerialName("viewedAdsChatMessage")
    var viewedAdsChatMessage: Int = 0,
    @SerialName("messageRequestWithProModel")
    var messageRequestWithProModel: Int = 0,
) {
    val remainingMessage: Int
        get() = max(availableMessages - times, 0)

    fun canGenerate(context: Context): Boolean {
        val app = context.applicationContext as AbstractApplication

        return remainingMessage > 0
    }
}


@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class ChatModelUsage(
    @SerialName("usedTimesMap")
    var usedTimesMap: MutableMap<String, ChatModelMessageDaily> = HashMap()
) {
    fun getUsage(key: ChatModel): Int {
        val default =
            ChatModelMessageDaily(date = Date().format(DatePattern.YYYY_DD_MM), times = 1)

        var daily = usedTimesMap.getOrDefault(key.key, default)

        if (daily.date != Date().format(DatePattern.YYYY_DD_MM)) {
            daily =
                ChatModelMessageDaily(date = Date().format(DatePattern.YYYY_DD_MM), times = 1)
        } else {
            daily.times += 1
        }

        return daily.times
    }

    fun getDailyModel(key: ChatModel): ChatModelMessageDaily {
        val default =
            ChatModelMessageDaily(date = Date().format(DatePattern.YYYY_DD_MM), times = 1)

        return usedTimesMap.getOrDefault(key.key, default)
    }

    fun getFreeTimes(key: ChatModel): Int {
        return 1
    }

    fun canGenerate(context: Context, key: ChatModel): Boolean {
        if (context.isPremium) {
            return true
        }

        val usage = getUsage(key)

        return usage < getFreeTimes(key)
    }
}




@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class ChatModelMessageDaily(
    @SerialName("date")
    val date: String,
    @SerialName("times")
    var times: Int
) {

    fun shouldSwitch(limit: Int): Boolean {
        return times >= limit
    }
}


@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class AdvanceToolUsage(
    @SerialName("usedTimesMap")
    var usedTimesMap: MutableMap<String, Int> = HashMap()
) : AppUsage<AdvanceToolType>() {
    override fun getUsage(key: AdvanceToolType): Int {
        return usedTimesMap.getOrDefault(key.key, 0)
    }
}