package com.wodox.domain.home.usecase.subtask

import com.wodox.domain.base.BaseParamsUnsafeUseCase
import com.wodox.domain.home.model.local.SubTask
import com.wodox.domain.home.repository.SubTaskRepository
import javax.inject.Inject


class SaveSubTaskUseCase @Inject constructor(
    private val repository: SubTaskRepository
) : BaseParamsUnsafeUseCase<SubTask, SubTask?>() {
    override suspend fun execute(params: SubTask): SubTask? {
        return repository.save(params)
    }
}
