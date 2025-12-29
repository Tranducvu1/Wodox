package com.wodox.domain.home.usecase.comment

import com.wodox.domain.base.BaseParamsFlowUnsafeUseCase
import com.wodox.domain.home.model.local.Comment
import com.wodox.domain.home.repository.CommentRepository
import kotlinx.coroutines.flow.Flow
import java.util.UUID

class GetAllCommentByTaskIdUseCase(
    private val repository: CommentRepository
) : BaseParamsFlowUnsafeUseCase<UUID, List<Comment>>() {

    override suspend fun execute(params: UUID): Flow<List<Comment>> {
        return repository.getAllCommentByTaskId(params)
    }
}
