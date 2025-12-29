package com.wodox.domain.main.usecase

import com.wodox.domain.common.base.BaseNoParamsFlowUnsafeUseCase
import com.wodox.domain.main.model.Item
import com.wodox.domain.main.model.getDefaultProfile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf


class GetAllItemProfileSetting() : BaseNoParamsFlowUnsafeUseCase<List<Item>>() {
    override suspend fun execute(): Flow<List<Item>> {
        val defaultItems = getDefaultProfile()
        return flowOf(defaultItems)
    }
}