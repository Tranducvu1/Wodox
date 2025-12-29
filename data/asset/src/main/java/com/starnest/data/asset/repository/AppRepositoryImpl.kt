package com.starnest.data.asset.repository

import com.starnest.data.asset.datasource.MessageTemplateAssetDataSource
import com.starnest.data.asset.datasource.mapper.CategoryMapper
import com.starnest.data.asset.datasource.mapper.MessageTemplateMapper
import com.wodox.domain.asset.model.Category
import com.wodox.domain.asset.model.MessageTemplate
import com.starnest.domain.asset.repository.AppRepository

class AppRepositoryImpl(
    val messageTemplateMapper: MessageTemplateMapper,
    val categoryMapper: CategoryMapper,
    val messageTemplateAssetDataSource: MessageTemplateAssetDataSource
) : AppRepository {
    override suspend fun getMessageTemplateCategories(): List<Category> {
        return messageTemplateAssetDataSource.getCategories()
            .let { categoryMapper.mapToDomainList(it) }
    }

    override suspend fun getMessageTemplates(category: Category?): List<MessageTemplate> {
        return messageTemplateAssetDataSource.getData(category?.let { categoryMapper.mapToEntity(it) })
            .let { messageTemplateMapper.mapToDomainList(it) }
    }
}