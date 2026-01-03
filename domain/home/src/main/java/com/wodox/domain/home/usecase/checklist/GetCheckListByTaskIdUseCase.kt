package com.wodox.domain.home.usecase.checklist

import com.wodox.domain.base.BaseParamsFlowUnsafeUseCase
import com.wodox.domain.home.model.local.CheckList
import com.wodox.domain.home.repository.CheckListRepository
import kotlinx.coroutines.flow.Flow
import java.util.UUID


class GetCheckListByTaskIdUseCase(
    private val repository: CheckListRepository
) : BaseParamsFlowUnsafeUseCase<String, List<CheckList>>() {
    override suspend fun execute(params: String): Flow<List<CheckList>> {
        return repository.getAllCheckListByTaskID(params)
    }
}