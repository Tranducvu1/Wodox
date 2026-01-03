package com.wodox.domain.home.repository

import com.wodox.domain.home.model.local.Comment
import kotlinx.coroutines.flow.Flow
import java.util.UUID

interface CommentRepository {

    suspend fun save(comment: Comment): Comment?

    suspend fun getAllCommentByTaskId(taskId: UUID): Flow<List<Comment>>

    suspend fun deleteComment(commentId: UUID)

    suspend fun deleteCommentByTaskId(taskId: UUID)

    suspend fun getLatestUnreadComment(userId: UUID): Flow<Comment?>


    suspend fun updateComment(commentId: UUID, newContent: String): Boolean


    suspend fun getCommentCountByTaskId(taskId: UUID): Int

    suspend fun getLatestCommentByTaskId(taskId: UUID): Comment?
}

