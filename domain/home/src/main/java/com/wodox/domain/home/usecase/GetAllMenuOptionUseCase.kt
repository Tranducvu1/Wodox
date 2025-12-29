package com.wodox.domain.home.usecase

import com.wodox.domain.base.BaseNoParamsFlowUnsafeUseCase
import com.wodox.domain.home.model.local.MenuOption
import com.wodox.domain.home.model.local.getDefaultItemsMenu
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class GetAllMenuOptionUseCase() : BaseNoParamsFlowUnsafeUseCase<List<MenuOption>>() {
    override suspend fun execute(): Flow<List<MenuOption>> {
        val defaultItems = getDefaultItemsMenu()
        return flowOf(defaultItems)
    }
}