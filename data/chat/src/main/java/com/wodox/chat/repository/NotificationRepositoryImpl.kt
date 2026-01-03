package com.wodox.chat.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.wodox.chat.mapper.NotificationMapper
import com.wodox.chat.model.NotificationEntity
import com.wodox.domain.chat.model.local.Notification
import com.wodox.domain.chat.repository.NotificationRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.Date
import java.util.UUID
import javax.inject.Inject

class NotificationRepositoryImpl @Inject constructor(
    private val mapper: NotificationMapper,
) : NotificationRepository {

    private val TAG = "NotificationRepository"

    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    private val notificationsCollection = firestore.collection("notifications")

    override suspend fun save(notification: Notification): Notification? {
        android.util.Log.d(TAG, "========================================")
        android.util.Log.d(TAG, "SAVE NOTIFICATION - START")
        android.util.Log.d(TAG, "========================================")
        android.util.Log.d(TAG, "Notification ID: ${notification.id}")
        android.util.Log.d(TAG, "User ID: ${notification.userId}")
        android.util.Log.d(TAG, "From User ID: ${notification.fromUserId}")
        android.util.Log.d(TAG, "Content: ${notification.content}")
        android.util.Log.d(TAG, "Content: ${notification.content}")
        android.util.Log.d(TAG, "Is Read: ${notification.isRead}")
        android.util.Log.d(TAG, "Created At: ${notification.createdAt}")
        android.util.Log.d(TAG, "Deleted At: ${notification.deletedAt}")

        return try {
            android.util.Log.d(TAG, "Mapping notification to entity...")
            val entity = mapper.mapToEntity(notification).apply {
                this.updatedAt = Date()
            }

            android.util.Log.d(TAG, "----------------------------------------")
            android.util.Log.d(TAG, "ENTITY MAPPED:")
            android.util.Log.d(TAG, "Entity ID: ${entity.id}")
            android.util.Log.d(TAG, "Entity User ID: ${entity.userId}")
            android.util.Log.d(TAG, "Entity From User ID: ${entity.fromUserId}")
            android.util.Log.d(TAG, "Entity content: ${entity.content}")
            android.util.Log.d(TAG, "Entity Content: ${entity.content}")
            android.util.Log.d(TAG, "Entity Is Read: ${entity.isRead}")
            android.util.Log.d(TAG, "Entity Created At: ${entity.createdAt}")
            android.util.Log.d(TAG, "Entity Updated At: ${entity.updatedAt}")
            android.util.Log.d(TAG, "Entity Deleted At: ${entity.deletedAt}")
            android.util.Log.d(TAG, "----------------------------------------")

            android.util.Log.d(
                TAG,
                "Saving to Firestore collection: ${notificationsCollection.path}"
            )
            android.util.Log.d(TAG, "Document ID: ${entity.id}")

            notificationsCollection
                .document(entity.id.toString())
                .set(entity)
                .await()

            android.util.Log.d(TAG, "✅ NOTIFICATION SAVED SUCCESSFULLY!")

            // Verify saved document
            android.util.Log.d(TAG, "Verifying saved notification...")
            val savedDoc = notificationsCollection
                .document(entity.id.toString())
                .get()
                .await()

            if (savedDoc.exists()) {
                android.util.Log.d(TAG, "✅ Document exists in Firestore")
                android.util.Log.d(TAG, "Document data: ${savedDoc.data}")
                android.util.Log.d(TAG, "  userId: ${savedDoc.getString("userId")}")
                android.util.Log.d(TAG, "  fromUserId: ${savedDoc.getString("fromUserId")}")
                android.util.Log.d(TAG, "  type: ${savedDoc.getString("type")}")
                android.util.Log.d(TAG, "  content: ${savedDoc.getString("content")}")
                android.util.Log.d(TAG, "  isRead: ${savedDoc.getBoolean("isRead")}")
            } else {
                android.util.Log.e(TAG, "❌ Document NOT found in Firestore!")
            }

            android.util.Log.d(TAG, "========================================")

            mapper.mapToDomain(entity)

        } catch (e: Exception) {
            android.util.Log.e(TAG, "========================================")
            android.util.Log.e(TAG, "❌ ERROR SAVING NOTIFICATION")
            android.util.Log.e(TAG, "Error message: ${e.message}")
            android.util.Log.e(TAG, "Error cause: ${e.cause}")
            android.util.Log.e(TAG, "Stack trace:")
            e.printStackTrace()
            android.util.Log.e(TAG, "========================================")
            null
        }
    }

    override suspend fun insertAll(notifications: List<Notification>) {
        try {
            val batch = firestore.batch()

            notifications.forEach { notification ->
                val entity = mapper.mapToEntity(notification)
                val docRef = notificationsCollection.document(entity.id.toString())
                batch.set(docRef, entity)
            }

            batch.commit().await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun getNotificationByUserId(userId: UUID): Flow<List<Notification>> = callbackFlow {
        val userIdStr = userId.toString()
        val listener = notificationsCollection
            .whereEqualTo("userId", userIdStr)
            .whereEqualTo("deletedAt", null)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    android.util.Log.e(TAG, "❌ Query error", error)
                    close(error)
                    return@addSnapshotListener
                }

                android.util.Log.d(TAG, "📥 Documents: ${snapshot?.documents?.size ?: 0}")

                val notifications = snapshot?.documents?.mapNotNull { doc ->
                    try {
                        doc.toObject(NotificationEntity::class.java)?.let { entity ->
                            mapper.mapToDomain(entity)
                        }
                    } catch (e: Exception) {
                        android.util.Log.e(TAG, "Error parsing doc", e)
                        null
                    }
                } ?: emptyList()

                android.util.Log.d(TAG, "✅ Mapped ${notifications.size} notifications")
                trySend(notifications)
            }

        awaitClose { listener.remove() }
    }

    override fun getNotificationByTaskId(taskId: UUID): Flow<List<Notification>> = callbackFlow {
        val taskIdStr = taskId.toString() // ← String

        val listener = notificationsCollection
            .whereEqualTo("taskId", taskIdStr) // ← String query
            .whereEqualTo("isRead", true)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val notifications = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(NotificationEntity::class.java)?.let { entity ->
                        mapper.mapToDomain(entity)
                    }
                } ?: emptyList()

                trySend(notifications)
            }

        awaitClose { listener.remove() }
    }

    override suspend fun markAsRead(notificationId: UUID) {
        try {
            Log.d("NotificationRepo", "🔔 markAsRead START - id=$notificationId")

            notificationsCollection
                .document(notificationId.toString())
                .update(
                    mapOf(
                        "isRead" to true,
                        "readAt" to Date()
                    )
                )
                .await()

            Log.d(
                "NotificationRepo",
                "✅ markAsRead SUCCESS - id=$notificationId, isRead=true, readAt=${Date()}"
            )
        } catch (e: Exception) {
            Log.e(
                "NotificationRepo",
                "❌ markAsRead FAILED - id=$notificationId, error=${e.message}",
                e
            )
        }
    }



    override fun getByTask(taskId: UUID): Flow<List<Notification>> = callbackFlow {
        val listener = notificationsCollection
            .whereEqualTo("taskId", taskId.toString())
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val notifications = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(NotificationEntity::class.java)?.let { entity ->
                        mapper.mapToDomain(entity)
                    }
                } ?: emptyList()

                trySend(notifications)
            }

        awaitClose { listener.remove() }
    }


    override suspend fun markAllAsRead(userId: UUID) {
        try {
            val snapshot = notificationsCollection
                .whereEqualTo("userId", userId.toString())
                .whereEqualTo("isRead", false)
                .get()
                .await()

            val batch = firestore.batch()
            val readAt = Date()

            snapshot.documents.forEach { doc ->
                batch.update(
                    doc.reference,
                    mapOf(
                        "isRead" to true,
                        "readAt" to readAt
                    )
                )
            }

            batch.commit().await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override suspend fun update(notification: Notification) {
        try {
            val entity = mapper.mapToEntity(notification)
            notificationsCollection
                .document(entity.id.toString())
                .set(entity)
                .await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override suspend fun updateAll(notifications: List<Notification>) {
        try {
            val batch = firestore.batch()

            notifications.forEach { notification ->
                val entity = mapper.mapToEntity(notification)
                val docRef = notificationsCollection.document(entity.id.toString())
                batch.set(docRef, entity)
            }

            batch.commit().await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}