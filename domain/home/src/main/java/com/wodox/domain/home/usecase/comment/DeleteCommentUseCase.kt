package com.wodox.domain.home.usecase.comment

import com.wodox.domain.base.BaseParamsUnsafeUseCase
import com.wodox.domain.home.repository.CommentRepository
import java.util.UUID
import javax.inject.Inject

class DeleteCommentUseCase @Inject constructor(
    private val repository: CommentRepository
) : BaseParamsUnsafeUseCase<UUID, Unit>() {

    override suspend fun execute(params: UUID) {
        repository.deleteComment(params)
    }
}