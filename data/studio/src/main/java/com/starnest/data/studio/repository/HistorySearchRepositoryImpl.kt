package com.starnest.data.studio.repository

import com.starnest.data.studio.shareprefs.HistorySearchPrefsDataSource
import com.starnest.domain.studio.repository.HistorySearchRepository

class HistorySearchRepositoryImpl(
    private val localDataSource: HistorySearchPrefsDataSource,
) : HistorySearchRepository {
    override suspend fun getAll(): List<String> {
        return localDataSource.getHistory()
    }

    override suspend fun save(historySearch: List<String>) {
        return localDataSource.saveHistory(historySearch)
    }
}