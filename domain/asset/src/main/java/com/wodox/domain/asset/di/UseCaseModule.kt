package com.starnest.domain.asset.di

import com.starnest.domain.asset.repository.AppRepository
import com.wodox.domain.asset.usecase.GetMessageTemplateCategoriesUseCase
import com.starnest.domain.asset.usecase.GetMessageTemplatesUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {
    @Provides
    @Singleton
    fun provideGetMessageTemplateUseCase(repository: AppRepository): GetMessageTemplatesUseCase {
        return GetMessageTemplatesUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideGetMessageTemplateCategoryUseCase(repository: AppRepository): GetMessageTemplateCategoriesUseCase {
        return GetMessageTemplateCategoriesUseCase(repository)
    }
}