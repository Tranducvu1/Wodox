package com.starnest.data.asset.datasource

import android.content.Context
import com.wodox.core.extension.FileUtils
import com.starnest.data.asset.datasource.model.CategoryDto
import com.starnest.data.asset.datasource.model.MessageTemplateDto
import com.wodox.data.common.datasource.AppSharePrefs
import com.wodox.core.data.util.JsonUtils
import com.wodox.core.extension.toArrayList

class MessageTemplateAssetDataSourceImpl(
    val context: Context,
    val appSharePrefs: AppSharePrefs
) : MessageTemplateAssetDataSource {
    private val templates = HashMap<String, ArrayList<MessageTemplateDto>>()

    override suspend fun getCategories(): List<CategoryDto> {
        val data = getData(null)

        return data.map { it.category }.distinct().mapIndexed { index, category ->
            CategoryDto(
                id = "${index + 1}",
                name = category
            )
        }
    }

    override suspend fun getData(category: CategoryDto?): List<MessageTemplateDto> {
        val language = appSharePrefs.currentCodeLang

        var data: ArrayList<MessageTemplateDto> = templates[language] ?: arrayListOf()

        if (data.isEmpty()) {
            data = loadData() ?: arrayListOf()
            this.templates[language] = data
        }

        if (category != null) {
            data = data.filter { it.category == category.name }.toArrayList()
        }

        mapOutputLanguage(data = data) { item, translatedItems ->
            val translatedItem = translatedItems.firstOrNull { it.id == item.id }

            translatedItem?.let {
                item.name = it.name
            }
        }

        return data
    }

    private fun loadData(): ArrayList<MessageTemplateDto>? {
        val language = appSharePrefs.currentCodeLang

        return try {
            val rawData = FileUtils.loadJsonFromAsset(
                context,
                "data/${FILE_NAME}_$language.json"
            )
            JsonUtils.parse(rawData) ?: arrayListOf()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun mapOutputLanguage(
        data: ArrayList<MessageTemplateDto>,
        block: (MessageTemplateDto, ArrayList<MessageTemplateDto>) -> Unit
    ) {
        val outputLanguage = appSharePrefs.currentCodeLang

        try {
            val rawData = FileUtils.loadJsonFromAsset(
                context,
                "data/${FILE_NAME}_$outputLanguage.json"
            )

            val translatedData: ArrayList<MessageTemplateDto> =
                JsonUtils.parse(rawData) ?: return

            for (item in data) {
                block.invoke(item, translatedData)
            }
        } catch (_: Exception) {
        }
    }

    companion object {
        const val FILE_NAME = "template"
    }
}