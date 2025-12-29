package com.starnest.data.studio.di

import com.starnest.data.studio.datasource.mapper.CategoryStudioMapper
import com.starnest.data.studio.datasource.mapper.DrawingItemMapper
import com.starnest.data.studio.datasource.mapper.DrawingLayerMapper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MapperModule {
    @Singleton
    @Provides
    fun providerCategoryStudioMapper(): CategoryStudioMapper {
        return CategoryStudioMapper()
    }

    @Singleton
    @Provides
    fun providerDrawingItemMapper(): DrawingItemMapper {
        return DrawingItemMapper()
    }

    @Singleton
    @Provides
    fun providerDrawingLayerMapper(): DrawingLayerMapper {
        return DrawingLayerMapper()
    }


}