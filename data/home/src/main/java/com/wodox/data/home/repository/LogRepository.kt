package com.wodox.data.home.repository

import android.util.Log as AndroidLog
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.wodox.data.home.datasource.local.database.task.dao.LogDao
import com.wodox.data.home.datasource.local.database.task.mapper.LogMapper
import com.wodox.domain.home.model.local.Log
import com.wodox.domain.home.repository.LogRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import java.util.Date
import java.util.UUID
import javax.inject.Inject

class LogRepositoryImpl @Inject constructor(
    private val dao: LogDao,
    private val mapper: LogMapper,
) : LogRepository {

    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val logsCollection = firestore.collection("logs")

    override suspend fun save(log: Log): Log? {
        return try {
            val entity = mapper.mapToEntity(log).apply {
                this.updatedAt = Date()
            }
            dao.save(entity)

            saveLogToFirestore(log.copy(updatedAt = entity.updatedAt))

            val savedLog = mapper.mapToDomain(entity)
            AndroidLog.d("LogRepository", "✅ Saved log '${savedLog.title}' (Local + Firestore)")

            savedLog
        } catch (e: Exception) {
            AndroidLog.e("LogRepository", "Error saving log: ${e.message}", e)
            null
        }
    }

    override suspend fun getAllLog(taskId: UUID): Flow<List<Log>> {
        return dao.getLogByTaskId(taskId).map { entities ->
            mapper.mapToDomainList(entities)
        }
    }

    private suspend fun saveLogToFirestore(log: Log): Boolean {
        return try {
            val logData = mapOf(
                "id" to log.id.toString(),
                "taskId" to log.taskId.toString(),
                "title" to log.title,
                "description" to log.description,
                "createdAt" to log.createdAt,
                "updatedAt" to log.updatedAt,
                "deletedAt" to log.deletedAt
            )

            logsCollection
                .document(log.id.toString())
                .set(logData)
                .await()

            AndroidLog.d("LogRepository", "✅ Log saved to Firestore: ${log.title}")
            true
        } catch (e: Exception) {
            AndroidLog.e("LogRepository", "❌ Error saving log to Firestore: ${e.message}", e)
            false
        }
    }

    override suspend fun loadLogsFromFirestoreByTaskId(taskId: UUID): List<Log> {
        return try {
            AndroidLog.d("LogRepository", "📥 Loading logs from Firestore for taskId: $taskId")

            val snapshot = logsCollection
                .whereEqualTo("taskId", taskId.toString())
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .await()

            val logs = snapshot.documents.mapNotNull { doc ->
                try {
                    Log(
                        id = UUID.fromString(doc.getString("id") ?: return@mapNotNull null),
                        taskId = UUID.fromString(doc.getString("taskId") ?: return@mapNotNull null),
                        title = doc.getString("title"),
                        description = doc.getString("description"),
                        createdAt = doc.getTimestamp("createdAt")?.toDate() ?: Date(),
                        updatedAt = doc.getTimestamp("updatedAt")?.toDate() ?: Date(),
                        deletedAt = doc.getTimestamp("deletedAt")?.toDate()
                    )
                } catch (e: Exception) {
                    AndroidLog.e("LogRepository", "Error parsing Log from Firestore", e)
                    null
                }
            }

            AndroidLog.d("LogRepository", "✅ Loaded ${logs.size} logs from Firestore")

            logs.forEach { log ->
                val entity = mapper.mapToEntity(log)
                dao.save(entity)
            }

            logs
        } catch (e: Exception) {
            AndroidLog.e("LogRepository", "❌ Error loading logs from Firestore: ${e.message}", e)
            emptyList()
        }
    }

    override suspend fun deleteLogFromFirestore(logId: UUID): Boolean {
        return try {
            logsCollection
                .document(logId.toString())
                .update("deletedAt", Date())
                .await()

            AndroidLog.d("LogRepository", "✅ Log soft deleted from Firestore: $logId")
            true
        } catch (e: Exception) {
            AndroidLog.e("LogRepository", "❌ Error deleting log from Firestore: ${e.message}", e)
            false
        }
    }

    override suspend fun deleteAllLogsByTaskIdFromFirestore(taskId: UUID): Boolean {
        return try {
            val snapshot = logsCollection
                .whereEqualTo("taskId", taskId.toString())
                .get()
                .await()

            snapshot.documents.forEach { doc ->
                doc.reference.delete().await()
            }

            AndroidLog.d("LogRepository", "✅ Deleted all logs for task: $taskId")
            true
        } catch (e: Exception) {
            AndroidLog.e("LogRepository", "❌ Error deleting logs by taskId: ${e.message}", e)
            false
        }
    }

}