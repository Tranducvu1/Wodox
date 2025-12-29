package com.wodox.data.docs.di

import com.wodox.data.docs.repository.SharedDocumentMapper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ShareDocumentMapperModule {

    @Singleton
    @Provides
    fun providerShareDocumentMapper(
    ): SharedDocumentMapper {
        return SharedDocumentMapper()
    }
}