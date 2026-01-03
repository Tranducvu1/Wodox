package com.wodox.domain.home.usecase.comment

import com.wodox.domain.base.BaseParamsUnsafeUseCase
import com.wodox.domain.home.repository.CommentRepository
import javax.inject.Inject

data class UpdateCommentParams(
    val commentId: java.util.UUID,
    val newContent: String
)

class UpdateCommentUseCase @Inject constructor(
    private val repository: CommentRepository
) : BaseParamsUnsafeUseCase<UpdateCommentParams, Boolean>() {
    override suspend fun execute(params: UpdateCommentParams): Boolean {
        return repository.updateComment(params.commentId, params.newContent)
    }
}