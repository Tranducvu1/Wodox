package com.starnest.data.asset.di

import com.starnest.data.asset.datasource.MessageTemplateAssetDataSource
import com.starnest.data.asset.datasource.mapper.CategoryMapper
import com.starnest.data.asset.datasource.mapper.MessageTemplateMapper
import com.starnest.data.asset.repository.AppRepositoryImpl
import com.starnest.domain.asset.repository.AppRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Singleton
    @Provides
    fun provideAppRepository(
        messageTemplateAssetDataSource: MessageTemplateAssetDataSource,
        messageTemplateMapper: MessageTemplateMapper,
        categoryMapper: CategoryMapper
    ): AppRepository {
        return AppRepositoryImpl(
            messageTemplateAssetDataSource = messageTemplateAssetDataSource,
            messageTemplateMapper = messageTemplateMapper,
            categoryMapper = categoryMapper
        )
    }
}