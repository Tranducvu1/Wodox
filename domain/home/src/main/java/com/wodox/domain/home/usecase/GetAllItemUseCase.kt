package com.wodox.domain.home.usecase

import com.wodox.domain.base.BaseNoParamsFlowUnsafeUseCase
import com.wodox.domain.home.model.local.Item
import com.wodox.domain.home.model.local.getDefaultItems
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class GetAllItemUseCase() : BaseNoParamsFlowUnsafeUseCase<List<Item>>() {
    override suspend fun execute(): Flow<List<Item>> {
        val defaultItems = getDefaultItems()
        return flowOf(defaultItems)
    }
}