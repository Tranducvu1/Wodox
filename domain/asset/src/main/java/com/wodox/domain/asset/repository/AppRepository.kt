package com.starnest.domain.asset.repository

import com.wodox.domain.asset.model.Category
import com.wodox.domain.asset.model.MessageTemplate

interface AppRepository {
    suspend fun getMessageTemplateCategories(): List<Category>

    suspend fun getMessageTemplates(category: Category? = null): List<MessageTemplate>
}