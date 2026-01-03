package com.wodox.data.home.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.wodox.data.home.datasource.local.database.task.dao.TaskAssignDao
import com.wodox.data.home.datasource.local.database.task.mapper.TaskAssigneeMapper
import com.wodox.domain.home.model.local.TaskAssignee
import com.wodox.domain.home.repository.TaskAssignRepository
import kotlinx.coroutines.tasks.await
import java.util.Date
import java.util.UUID
import javax.inject.Inject

class TaskAssignRepositoryImpl @Inject constructor(
    private val taskAssignDao: TaskAssignDao,
    private val taskAssigneeMapper: TaskAssigneeMapper,
) : TaskAssignRepository {

    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val taskAssigneesCollection = firestore.collection("task_assignees")

    override suspend fun assignUserToTask(taskAssignee: TaskAssignee): TaskAssignee {
        val entity = taskAssigneeMapper.mapToEntity(taskAssignee)
        taskAssignDao.insertTaskAssignee(entity)

        saveTaskAssigneeToFirestore(taskAssignee)

        return taskAssigneeMapper.mapToDomain(entity)
    }

    override suspend fun getTaskAssignByTaskID(id: UUID): TaskAssignee? {
        val entity = taskAssignDao.getTaskAssignByTaskId(id)
        return entity?.let { taskAssigneeMapper.mapToDomain(it) }
    }

    override suspend fun getTaskAssignByUserID(userId: UUID): List<TaskAssignee>? {
        val entity = taskAssignDao.getTaskAssignByUserId(userId)
        return entity.map { taskAssigneeMapper.mapToDomain(it) }
    }


    private suspend fun saveTaskAssigneeToFirestore(taskAssignee: TaskAssignee): Boolean {
        return try {
            val assigneeData = mapOf(
                "id" to taskAssignee.id.toString(),
                "taskId" to taskAssignee.taskId.toString(),
                "userId" to taskAssignee.userId.toString(),
                "ownerId" to taskAssignee.ownerId.toString(),
                "assignedAt" to taskAssignee.assignedAt,
                "deletedAt" to taskAssignee.deletedAt
            )

            taskAssigneesCollection
                .document(taskAssignee.id.toString())
                .set(assigneeData)
                .await()

            Log.d("TaskAssignRepository", "✅ TaskAssignee saved to Firestore: ${taskAssignee.id}")
            true
        } catch (e: Exception) {
            Log.e("TaskAssignRepository", "❌ Error saving TaskAssignee to Firestore: ${e.message}", e)
            false
        }
    }


    suspend fun loadTaskAssigneesFromFirestoreByTaskId(taskId: UUID): List<TaskAssignee> {
        return try {
            Log.d("TaskAssignRepository", "📥 Loading task assignees from Firestore for taskId: $taskId")

            val snapshot = taskAssigneesCollection
                .whereEqualTo("taskId", taskId.toString())
                .get()
                .await()

            val assignees = snapshot.documents.mapNotNull { doc ->
                try {
                    TaskAssignee(
                        id = UUID.fromString(doc.getString("id") ?: return@mapNotNull null),
                        taskId = UUID.fromString(doc.getString("taskId") ?: return@mapNotNull null),
                        userId = UUID.fromString(doc.getString("userId") ?: return@mapNotNull null),
                        ownerId = UUID.fromString(doc.getString("ownerId") ?: return@mapNotNull null),
                        assignedAt = doc.getTimestamp("assignedAt")?.toDate() ?: Date(),
                        deletedAt = doc.getTimestamp("deletedAt")?.toDate()
                    )
                } catch (e: Exception) {
                    Log.e("TaskAssignRepository", "Error parsing TaskAssignee from Firestore", e)
                    null
                }
            }

            Log.d("TaskAssignRepository", "✅ Loaded ${assignees.size} assignees from Firestore")

            assignees.forEach { assignee ->
                val entity = taskAssigneeMapper.mapToEntity(assignee)
                taskAssignDao.insertTaskAssignee(entity)
            }

            assignees
        } catch (e: Exception) {
            Log.e("TaskAssignRepository", "❌ Error loading assignees from Firestore: ${e.message}", e)
            emptyList()
        }
    }


    suspend fun loadTaskAssigneesFromFirestoreByUserId(userId: UUID): List<TaskAssignee> {
        return try {
            Log.d("TaskAssignRepository", "📥 Loading task assignees from Firestore for userId: $userId")

            val snapshot = taskAssigneesCollection
                .whereEqualTo("userId", userId.toString())
                .get()
                .await()

            val assignees = snapshot.documents.mapNotNull { doc ->
                try {
                    TaskAssignee(
                        id = UUID.fromString(doc.getString("id") ?: return@mapNotNull null),
                        taskId = UUID.fromString(doc.getString("taskId") ?: return@mapNotNull null),
                        userId = UUID.fromString(doc.getString("userId") ?: return@mapNotNull null),
                        ownerId = UUID.fromString(doc.getString("ownerId") ?: return@mapNotNull null),
                        assignedAt = doc.getTimestamp("assignedAt")?.toDate() ?: Date(),
                        deletedAt = doc.getTimestamp("deletedAt")?.toDate()
                    )
                } catch (e: Exception) {
                    Log.e("TaskAssignRepository", "Error parsing TaskAssignee from Firestore", e)
                    null
                }
            }

            Log.d("TaskAssignRepository", "✅ Loaded ${assignees.size} assignees from Firestore")

            assignees.forEach { assignee ->
                val entity = taskAssigneeMapper.mapToEntity(assignee)
                taskAssignDao.insertTaskAssignee(entity)
            }

            assignees
        } catch (e: Exception) {
            Log.e("TaskAssignRepository", "❌ Error loading assignees from Firestore: ${e.message}", e)
            emptyList()
        }
    }


    suspend fun loadTaskAssigneesFromFirestoreByOwnerId(ownerId: UUID): List<TaskAssignee> {
        return try {
            Log.d("TaskAssignRepository", "📥 Loading task assignees from Firestore for ownerId: $ownerId")

            val snapshot = taskAssigneesCollection
                .whereEqualTo("ownerId", ownerId.toString())
                .get()
                .await()

            val assignees = snapshot.documents.mapNotNull { doc ->
                try {
                    TaskAssignee(
                        id = UUID.fromString(doc.getString("id") ?: return@mapNotNull null),
                        taskId = UUID.fromString(doc.getString("taskId") ?: return@mapNotNull null),
                        userId = UUID.fromString(doc.getString("userId") ?: return@mapNotNull null),
                        ownerId = UUID.fromString(doc.getString("ownerId") ?: return@mapNotNull null),
                        assignedAt = doc.getTimestamp("assignedAt")?.toDate() ?: Date(),
                        deletedAt = doc.getTimestamp("deletedAt")?.toDate()
                    )
                } catch (e: Exception) {
                    Log.e("TaskAssignRepository", "Error parsing TaskAssignee from Firestore", e)
                    null
                }
            }

            Log.d("TaskAssignRepository", "✅ Loaded ${assignees.size} assignees from Firestore")

            assignees.forEach { assignee ->
                val entity = taskAssigneeMapper.mapToEntity(assignee)
                taskAssignDao.insertTaskAssignee(entity)
            }

            assignees
        } catch (e: Exception) {
            Log.e("TaskAssignRepository", "❌ Error loading assignees from Firestore: ${e.message}", e)
            emptyList()
        }
    }


    suspend fun deleteTaskAssigneeFromFirestore(assigneeId: UUID): Boolean {
        return try {
            taskAssigneesCollection
                .document(assigneeId.toString())
                .update("deletedAt", Date())
                .await()

            Log.d("TaskAssignRepository", "✅ TaskAssignee soft deleted from Firestore: $assigneeId")
            true
        } catch (e: Exception) {
            Log.e("TaskAssignRepository", "❌ Error deleting TaskAssignee from Firestore: ${e.message}", e)
            false
        }
    }

    suspend fun hardDeleteTaskAssigneeFromFirestore(assigneeId: UUID): Boolean {
        return try {
            taskAssigneesCollection
                .document(assigneeId.toString())
                .delete()
                .await()

            Log.d("TaskAssignRepository", "✅ TaskAssignee permanently deleted from Firestore: $assigneeId")
            true
        } catch (e: Exception) {
            Log.e("TaskAssignRepository", "❌ Error hard deleting TaskAssignee from Firestore: ${e.message}", e)
            false
        }
    }

    suspend fun syncTaskAssigneesFromFirestore(userId: UUID) {
        try {
            Log.d("TaskAssignRepository", "🔄 Syncing task assignees from Firestore...")
            loadTaskAssigneesFromFirestoreByUserId(userId)
            Log.d("TaskAssignRepository", "✅ Task assignees synced from Firestore")
        } catch (e: Exception) {
            Log.e("TaskAssignRepository", "❌ Error syncing task assignees: ${e.message}", e)
        }
    }
}