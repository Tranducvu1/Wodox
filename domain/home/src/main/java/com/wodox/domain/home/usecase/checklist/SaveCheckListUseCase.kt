package com.wodox.domain.home.usecase.checklist

import com.wodox.domain.base.BaseParamsUnsafeUseCase
import com.wodox.domain.home.model.local.CheckList
import com.wodox.domain.home.repository.CheckListRepository
import javax.inject.Inject

class SaveCheckListUseCase @Inject constructor(
    private val repository: CheckListRepository
) : BaseParamsUnsafeUseCase<CheckList, CheckList?>() {
    override suspend fun execute(params: CheckList): CheckList? {
        return repository.save(params)
    }
}
