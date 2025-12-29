package com.starnest.data.studio.shareprefs

interface HistorySearchPrefsDataSource {
    fun saveHistory(history: List<String>)
    fun getHistory(): List<String>
}