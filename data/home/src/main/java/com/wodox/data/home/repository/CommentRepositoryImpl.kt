package com.wodox.data.home.repository
import com.wodox.data.home.datasource.local.database.task.dao.CommentDao
import com.wodox.data.home.datasource.local.database.task.mapper.CommentMapper
import com.wodox.domain.home.model.local.Comment
import com.wodox.domain.home.repository.CommentRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Date
import java.util.UUID
import javax.inject.Inject

class CommentRepositoryImpl @Inject constructor(
    private val dao: CommentDao,
    private val mapper: CommentMapper,
) : CommentRepository {
    override suspend fun save(comment: Comment): Comment? {
        val entity = mapper.mapToEntity(comment).apply {
            this.updatedAt = Date()
        }
        dao.save(entity)
        return mapper.mapToDomain(entity)
    }


    override suspend fun getAllCommentByTaskId(taskId: UUID): Flow<List<Comment>> {
        return dao.getAllCommentByTaskId(taskId).map { entities ->
            mapper.mapToDomainList(entities)
        }
    }

    override suspend fun deleteComment(commentId: UUID) {
        dao.softDelete(commentId)
    }

    override suspend fun deleteCommentByTaskId(taskId: UUID) {
        dao.softDeleteByTaskId(taskId)
    }

    override suspend fun getLatestUnreadComment(userId: UUID): Flow<Comment?> {
        return dao.getLatestUnreadComment(userId).map { entity ->
            entity?.let { mapper.mapToDomain(it) }
        }
    }
}

