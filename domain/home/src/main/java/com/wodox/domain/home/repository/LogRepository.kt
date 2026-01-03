package com.wodox.domain.home.repository

import kotlinx.coroutines.flow.Flow
import java.util.UUID
import com.wodox.domain.home.model.local.Log

interface LogRepository {

    suspend fun save(log: Log): Log?

    suspend fun getAllLog(taskId: UUID): Flow<List<Log>>

    suspend fun loadLogsFromFirestoreByTaskId(taskId: UUID): List<Log>

    suspend fun deleteLogFromFirestore(logId: UUID): Boolean

    suspend fun deleteAllLogsByTaskIdFromFirestore(taskId: UUID): Boolean

}