package com.starnest.data.studio.shareprefs

import android.content.Context
import com.starnest.core.extension.set
import org.json.JSONArray
import javax.inject.Inject

class HistorySearchPrefsDataSourceImpl @Inject constructor(
    context: Context,
) : HistorySearchPrefsDataSource {
    private val sharedPrefs = context.getSharedPreferences(
        SHARED_PREFS_NAME,
        Context.MODE_PRIVATE
    )

    override fun saveHistory(history: List<String>) {
        val json = JSONArray(history).toString()
        sharedPrefs.set(HISTORY_SEARCH, json)
    }

    override fun getHistory(): List<String> {
        val jsonString = sharedPrefs.getString(HISTORY_SEARCH, null) ?: return emptyList()
        return try {
            val jsonArray = JSONArray(jsonString)
            List(jsonArray.length()) { index -> jsonArray.getString(index) }
        } catch (e: Exception) {
            emptyList()
        }
    }


    companion object {
        private const val SHARED_PREFS_NAME = "App.history_search"
        private const val HISTORY_SEARCH = "History_Search"
    }
}