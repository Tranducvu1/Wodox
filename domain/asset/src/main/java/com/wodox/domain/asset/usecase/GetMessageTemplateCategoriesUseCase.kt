package com.wodox.domain.asset.usecase

import com.wodox.domain.asset.model.Category
import com.starnest.domain.asset.repository.AppRepository
import com.wodox.domain.common.base.BaseNoParamsUnsafeUseCase

class GetMessageTemplateCategoriesUseCase(
    private val appRepository: AppRepository
) : BaseNoParamsUnsafeUseCase<List<Category>>() {
    override suspend fun execute(): List<Category> {
        val categories = appRepository.getMessageTemplateCategories()

        return categories
    }
}