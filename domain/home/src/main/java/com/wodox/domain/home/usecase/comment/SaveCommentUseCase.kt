package com.wodox.domain.home.usecase.comment

import com.wodox.domain.base.BaseParamsUnsafeUseCase
import com.wodox.domain.home.model.local.Comment
import com.wodox.domain.home.repository.CommentRepository
import javax.inject.Inject

class SaveCommentUseCase @Inject constructor(
    private val repository: CommentRepository
) : BaseParamsUnsafeUseCase<Comment, Comment?>() {

    override suspend fun execute(params: Comment): Comment? {
        return repository.save(params)
    }
}