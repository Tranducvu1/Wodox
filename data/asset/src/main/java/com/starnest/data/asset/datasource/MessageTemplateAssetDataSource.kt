package com.starnest.data.asset.datasource

import com.starnest.data.asset.datasource.model.CategoryDto
import com.starnest.data.asset.datasource.model.MessageTemplateDto

interface MessageTemplateAssetDataSource {
    suspend fun getCategories(): List<CategoryDto>

    suspend fun getData(category: CategoryDto?): List<MessageTemplateDto>
}
