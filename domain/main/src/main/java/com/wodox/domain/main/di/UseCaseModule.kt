package com.wodox.domain.main.di

import com.wodox.domain.home.usecase.GetAllMenuOptionUseCase
import com.wodox.domain.main.usecase.GetAllItemProfileSetting
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
    fun provideGetAllItemProfileSetting(
    ): GetAllItemProfileSetting {
        return GetAllItemProfileSetting()
    }

}