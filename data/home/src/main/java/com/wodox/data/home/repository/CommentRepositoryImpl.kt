package com.wodox.data.home.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.wodox.domain.home.model.local.Comment
import com.wodox.domain.home.repository.CommentRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.Date
import java.util.UUID
import javax.inject.Inject

class CommentRepositoryImpl @Inject constructor() : CommentRepository {

    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val commentsCollection = firestore.collection("comments")

    override suspend fun save(comment: Comment): Comment? {
        return try {
            val updatedComment = comment.copy(updatedAt = Date())

            val commentData = mapOf(
                "id" to updatedComment.id.toString(),
                "taskId" to updatedComment.taskId.toString(),
                "userId" to updatedComment.userId.toString(),
                "content" to updatedComment.content,
                "createdAt" to updatedComment.createdAt,
                "updatedAt" to updatedComment.updatedAt,
                "deletedAt" to updatedComment.deletedAt
            )

            commentsCollection
                .document(updatedComment.id.toString())
                .set(commentData)
                .await()

            Log.d("CommentRepository", "Comment saved to Firestore: ${updatedComment.id}")
            updatedComment
        } catch (e: Exception) {
            Log.e("CommentRepository", "Error saving comment: ${e.message}", e)
            null
        }
    }

    override suspend fun getAllCommentByTaskId(taskId: UUID): Flow<List<Comment>> = callbackFlow {
        val listener = commentsCollection
            .whereEqualTo("taskId", taskId.toString())
            .whereEqualTo("deletedAt", null)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e(
                        "CommentRepository",
                        "Error listening to comments: ${error.message}",
                        error
                    )
                    close(error)
                    return@addSnapshotListener
                }

                val comments = snapshot?.documents?.mapNotNull { doc ->
                    try {
                        Comment(
                            id = UUID.fromString(doc.getString("id") ?: return@mapNotNull null),
                            taskId = UUID.fromString(
                                doc.getString("taskId") ?: return@mapNotNull null
                            ),
                            userId = UUID.fromString(
                                doc.getString("userId") ?: return@mapNotNull null
                            ),
                            content = doc.getString("content") ?: "",
                            createdAt = doc.getTimestamp("createdAt")?.toDate() ?: Date(),
                            updatedAt = doc.getTimestamp("updatedAt")?.toDate() ?: Date(),
                            deletedAt = doc.getTimestamp("deletedAt")?.toDate()
                        )
                    } catch (e: Exception) {
                        Log.e("CommentRepository", "Error parsing comment: ${e.message}", e)
                        null
                    }
                } ?: emptyList()

                trySend(comments)
            }

        awaitClose { listener.remove() }
    }

    override suspend fun deleteComment(commentId: UUID) {
        try {
            commentsCollection
                .document(commentId.toString())
                .update("deletedAt", Date())
                .await()

            Log.d("CommentRepository", "Comment soft deleted: $commentId")
        } catch (e: Exception) {
            Log.e("CommentRepository", "Error deleting comment: ${e.message}", e)
        }
    }

    override suspend fun deleteCommentByTaskId(taskId: UUID) {
        try {
            val snapshot = commentsCollection
                .whereEqualTo("taskId", taskId.toString())
                .get()
                .await()

            snapshot.documents.forEach { doc ->
                doc.reference.update("deletedAt", Date()).await()
            }

            Log.d("CommentRepository", "All comments deleted for task: $taskId")
        } catch (e: Exception) {
            Log.e("CommentRepository", "Error deleting comments by taskId: ${e.message}", e)
        }
    }

    override suspend fun getLatestUnreadComment(userId: UUID): Flow<Comment?> = callbackFlow {
        val listener = commentsCollection
            .whereEqualTo("userId", userId.toString())
            .whereEqualTo("deletedAt", null)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .limit(1)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e(
                        "CommentRepository",
                        "Error listening to latest comment: ${error.message}",
                        error
                    )
                    close(error)
                    return@addSnapshotListener
                }

                val comment = snapshot?.documents?.firstOrNull()?.let { doc ->
                    try {
                        Comment(
                            id = UUID.fromString(doc.getString("id") ?: return@let null),
                            taskId = UUID.fromString(doc.getString("taskId") ?: return@let null),
                            userId = UUID.fromString(doc.getString("userId") ?: return@let null),
                            content = doc.getString("content") ?: "",
                            createdAt = doc.getTimestamp("createdAt")?.toDate() ?: Date(),
                            updatedAt = doc.getTimestamp("updatedAt")?.toDate() ?: Date(),
                            deletedAt = doc.getTimestamp("deletedAt")?.toDate()
                        )
                    } catch (e: Exception) {
                        Log.e("CommentRepository", "Error parsing latest comment: ${e.message}", e)
                        null
                    }
                }

                trySend(comment)
            }

        awaitClose { listener.remove() }
    }

    override suspend fun updateComment(commentId: UUID, newContent: String): Boolean {
        return try {
            commentsCollection
                .document(commentId.toString())
                .update(
                    mapOf(
                        "content" to newContent,
                        "updatedAt" to Date()
                    )
                )
                .await()

            Log.d("CommentRepository", " Comment updated: $commentId")
            true
        } catch (e: Exception) {
            Log.e("CommentRepository", " Error updating comment: ${e.message}", e)
            false
        }
    }



    override suspend fun getCommentCountByTaskId(taskId: UUID): Int {
        return try {
            val snapshot = commentsCollection
                .whereEqualTo("taskId", taskId.toString())
                .whereEqualTo("deletedAt", null)
                .get()
                .await()

            snapshot.size()
        } catch (e: Exception) {
            Log.e("CommentRepository", "Error getting comment count: ${e.message}", e)
            0
        }
    }


    override suspend fun getLatestCommentByTaskId(taskId: UUID): Comment? {
        return try {
            val snapshot = commentsCollection
                .whereEqualTo("taskId", taskId.toString())
                .whereEqualTo("deletedAt", null)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .limit(1)
                .get()
                .await()

            snapshot.documents.firstOrNull()?.let { doc ->
                Comment(
                    id = UUID.fromString(doc.getString("id") ?: return null),
                    taskId = UUID.fromString(doc.getString("taskId") ?: return null),
                    userId = UUID.fromString(doc.getString("userId") ?: return null),
                    content = doc.getString("content") ?: "",
                    createdAt = doc.getTimestamp("createdAt")?.toDate() ?: Date(),
                    updatedAt = doc.getTimestamp("updatedAt")?.toDate() ?: Date(),
                    deletedAt = doc.getTimestamp("deletedAt")?.toDate()
                )
            }
        } catch (e: Exception) {
            Log.e("CommentRepository", "Error getting latest comment: ${e.message}", e)
            null
        }
    }
}