package com.wodox.data.home.datasource.local.database.task.dao

import androidx.paging.PagingSource
import androidx.room.*
import com.wodox.data.home.datasource.local.database.task.entity.CommentEntity
import kotlinx.coroutines.flow.Flow
import java.util.UUID

@Dao
interface CommentDao {

    @Query(
        """
        SELECT * FROM Comment
        WHERE deletedAt IS NULL
        ORDER BY createdAt DESC
        """
    )
    fun getAllComments(): Flow<List<CommentEntity>>

    @Query(
        """
        SELECT * FROM Comment
        WHERE taskId = :taskId
        AND deletedAt IS NULL
        ORDER BY createdAt DESC
        """
    )
    fun getAllCommentByTaskId(taskId: UUID): Flow<List<CommentEntity>>

    @Query(
        """
        SELECT * FROM Comment
        WHERE taskId = :taskId
        AND deletedAt IS NULL
        ORDER BY createdAt DESC
        """
    )
    fun getCommentPaging(taskId: UUID): PagingSource<Int, CommentEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(comment: CommentEntity): Long

    @Update
    suspend fun update(comment: CommentEntity): Int

    @Transaction
    suspend fun save(comment: CommentEntity): Long {
        val id = insert(comment)
        return if (id == -1L) {
            update(comment).toLong()
        } else {
            id
        }
    }

    @Query(
        """
        UPDATE Comment
        SET deletedAt = CURRENT_TIMESTAMP
        WHERE id = :commentId
        """
    )
    suspend fun softDelete(commentId: UUID)

    @Query(
        """
        UPDATE Comment
        SET deletedAt = CURRENT_TIMESTAMP
        WHERE taskId = :taskId
        """
    )
    suspend fun softDeleteByTaskId(taskId: UUID)

    @Query("""
        SELECT c.*
        FROM Comment c
        INNER JOIN Task t ON c.taskId = t.id
        LEFT JOIN TaskAssignees ta ON t.id = ta.taskId
        WHERE (t.ownerId = :userId OR ta.userId = :userId)
        AND c.userId != :userId
        AND c.deletedAt IS NULL
        ORDER BY c.createdAt DESC
        LIMIT 1
    """)
    fun getLatestUnreadComment(userId: UUID): Flow<CommentEntity?>
}