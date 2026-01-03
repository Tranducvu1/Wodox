package com.wodox.data.home.repository

import android.util.Log
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.google.firebase.firestore.FirebaseFirestore
import com.wodox.data.home.datasource.local.database.task.dao.SubTaskDao
import com.wodox.data.home.datasource.local.database.task.mapper.SubTaskMapper
import com.wodox.domain.home.model.local.SubTask
import com.wodox.domain.home.model.local.TaskStatus
import com.wodox.domain.home.repository.SubTaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import java.util.Date
import java.util.UUID
import javax.inject.Inject

class SubTaskRepositoryImpl @Inject constructor(
    private val dao: SubTaskDao,
    private val mapper: SubTaskMapper
) : SubTaskRepository {

    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val subTasksCollection = firestore.collection("subtasks")

    override fun getAllSubTaskByTaskID(id: UUID): Flow<List<SubTask>> {
        return dao.getSubTasksByTaskId(id).map { list ->
            list.map { entity ->
                mapper.mapToDomain(entity)
            }
        }
    }

    override suspend fun save(task: SubTask): SubTask? {
        return try {
            val entity = mapper.mapToEntity(task).apply {
                this.updatedAt = Date()
            }
            dao.save(entity)

            saveSubTaskToFirestore(task.copy(updatedAt = entity.updatedAt))

            val savedSubTask = mapper.mapToDomain(entity)
            Log.d("SubTaskRepository", "✅ Saved subtask '${savedSubTask.title}' (Local + Firestore)")

            savedSubTask
        } catch (e: Exception) {
            Log.e("SubTaskRepository", "Error saving subtask: ${e.message}", e)
            null
        }
    }

    private suspend fun saveSubTaskToFirestore(subTask: SubTask): Boolean {
        return try {
            val subTaskData = mapOf(
                "id" to subTask.id.toString(),
                "taskId" to subTask.taskId.toString(),
                "title" to subTask.title,
                "description" to subTask.description,
                "status" to subTask.status.name,
                "priority" to subTask.priority,
                "startAt" to subTask.startAt,
                "dueAt" to subTask.dueAt,
                "estimateAt" to subTask.estimateAt,
                "createdAt" to subTask.createdAt,
                "updatedAt" to subTask.updatedAt,
                "deletedAt" to subTask.deletedAt
            )

            subTasksCollection
                .document(subTask.id.toString())
                .set(subTaskData)
                .await()

            Log.d("SubTaskRepository", "✅ SubTask saved to Firestore: ${subTask.title}")
            true
        } catch (e: Exception) {
            Log.e("SubTaskRepository", "❌ Error saving subtask to Firestore: ${e.message}", e)
            false
        }
    }

    suspend fun loadSubTasksFromFirestoreByTaskId(taskId: UUID): List<SubTask> {
        return try {
            Log.d("SubTaskRepository", "📥 Loading subtasks from Firestore for taskId: $taskId")

            val snapshot = subTasksCollection
                .whereEqualTo("taskId", taskId.toString())
                .get()
                .await()

            val subTasks = snapshot.documents.mapNotNull { doc ->
                try {
                    SubTask(
                        id = UUID.fromString(doc.getString("id") ?: return@mapNotNull null),
                        taskId = UUID.fromString(doc.getString("taskId") ?: return@mapNotNull null),
                        title = doc.getString("title") ?: "",
                        description = doc.getString("description"),
                        status = TaskStatus.valueOf(doc.getString("status") ?: "TODO"),
                        priority = doc.getLong("priority")?.toInt() ?: 0,
                        startAt = doc.getTimestamp("startAt")?.toDate(),
                        dueAt = doc.getTimestamp("dueAt")?.toDate(),
                        estimateAt = doc.getTimestamp("estimateAt")?.toDate(),
                        createdAt = doc.getTimestamp("createdAt")?.toDate() ?: Date(),
                        updatedAt = doc.getTimestamp("updatedAt")?.toDate() ?: Date(),
                        deletedAt = doc.getTimestamp("deletedAt")?.toDate()
                    )
                } catch (e: Exception) {
                    Log.e("SubTaskRepository", "Error parsing SubTask from Firestore", e)
                    null
                }
            }

            Log.d("SubTaskRepository", "✅ Loaded ${subTasks.size} subtasks from Firestore")

            subTasks.forEach { subTask ->
                val entity = mapper.mapToEntity(subTask)
                dao.save(entity)
            }

            subTasks
        } catch (e: Exception) {
            Log.e("SubTaskRepository", "❌ Error loading subtasks from Firestore: ${e.message}", e)
            emptyList()
        }
    }


    suspend fun loadAllSubTasksFromFirestore(taskIds: List<UUID>): List<SubTask> {
        return try {
            if (taskIds.isEmpty()) {
                Log.w("SubTaskRepository", "No task IDs provided")
                return emptyList()
            }

            Log.d("SubTaskRepository", "📥 Loading subtasks from Firestore for ${taskIds.size} tasks")

            val taskIdStrings = taskIds.map { it.toString() }

            val allSubTasks = mutableListOf<SubTask>()

            taskIdStrings.chunked(10).forEach { chunk ->
                val snapshot = subTasksCollection
                    .whereIn("taskId", chunk)
                    .get()
                    .await()

                val subTasks = snapshot.documents.mapNotNull { doc ->
                    try {
                        SubTask(
                            id = UUID.fromString(doc.getString("id") ?: return@mapNotNull null),
                            taskId = UUID.fromString(doc.getString("taskId") ?: return@mapNotNull null),
                            title = doc.getString("title") ?: "",
                            description = doc.getString("description"),
                            status = TaskStatus.valueOf(doc.getString("status") ?: "TODO"),
                            priority = doc.getLong("priority")?.toInt() ?: 0,
                            startAt = doc.getTimestamp("startAt")?.toDate(),
                            dueAt = doc.getTimestamp("dueAt")?.toDate(),
                            estimateAt = doc.getTimestamp("estimateAt")?.toDate(),
                            createdAt = doc.getTimestamp("createdAt")?.toDate() ?: Date(),
                            updatedAt = doc.getTimestamp("updatedAt")?.toDate() ?: Date(),
                            deletedAt = doc.getTimestamp("deletedAt")?.toDate()
                        )
                    } catch (e: Exception) {
                        Log.e("SubTaskRepository", "Error parsing SubTask from Firestore", e)
                        null
                    }
                }

                allSubTasks.addAll(subTasks)
            }

            Log.d("SubTaskRepository", "✅ Loaded ${allSubTasks.size} subtasks from Firestore")

            allSubTasks.forEach { subTask ->
                val entity = mapper.mapToEntity(subTask)
                dao.save(entity)
            }

            allSubTasks
        } catch (e: Exception) {
            Log.e("SubTaskRepository", "❌ Error loading subtasks from Firestore: ${e.message}", e)
            emptyList()
        }
    }


    suspend fun deleteSubTaskFromFirestore(subTaskId: UUID): Boolean {
        return try {
            // Soft delete: Cập nhật deletedAt
            subTasksCollection
                .document(subTaskId.toString())
                .update("deletedAt", Date())
                .await()

            Log.d("SubTaskRepository", "✅ SubTask soft deleted from Firestore: $subTaskId")
            true
        } catch (e: Exception) {
            Log.e("SubTaskRepository", "❌ Error deleting subtask from Firestore: ${e.message}", e)
            false
        }
    }

    suspend fun hardDeleteSubTaskFromFirestore(subTaskId: UUID): Boolean {
        return try {
            subTasksCollection
                .document(subTaskId.toString())
                .delete()
                .await()

            Log.d("SubTaskRepository", "✅ SubTask permanently deleted from Firestore: $subTaskId")
            true
        } catch (e: Exception) {
            Log.e("SubTaskRepository", "❌ Error hard deleting subtask from Firestore: ${e.message}", e)
            false
        }
    }

    suspend fun deleteAllSubTasksByTaskIdFromFirestore(taskId: UUID): Boolean {
        return try {
            val snapshot = subTasksCollection
                .whereEqualTo("taskId", taskId.toString())
                .get()
                .await()

            snapshot.documents.forEach { doc ->
                doc.reference.delete().await()
            }

            Log.d("SubTaskRepository", "✅ Deleted all subtasks for task: $taskId")
            true
        } catch (e: Exception) {
            Log.e("SubTaskRepository", "❌ Error deleting subtasks by taskId: ${e.message}", e)
            false
        }
    }

    suspend fun updateSubTaskStatusInFirestore(subTaskId: UUID, status: TaskStatus): Boolean {
        return try {
            subTasksCollection
                .document(subTaskId.toString())
                .update(
                    mapOf(
                        "status" to status.name,
                        "updatedAt" to Date()
                    )
                )
                .await()

            Log.d("SubTaskRepository", "✅ SubTask status updated in Firestore: $subTaskId -> $status")
            true
        } catch (e: Exception) {
            Log.e("SubTaskRepository", "❌ Error updating subtask status: ${e.message}", e)
            false
        }
    }

    suspend fun syncSubTasksFromFirestore(taskId: UUID) {
        try {
            Log.d("SubTaskRepository", "🔄 Syncing subtasks from Firestore for task: $taskId")
            loadSubTasksFromFirestoreByTaskId(taskId)
            Log.d("SubTaskRepository", "✅ SubTasks synced from Firestore")
        } catch (e: Exception) {
            Log.e("SubTaskRepository", "❌ Error syncing subtasks: ${e.message}", e)
        }
    }
}