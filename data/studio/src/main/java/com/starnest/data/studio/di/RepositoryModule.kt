package com.starnest.data.studio.di

import DrawItemRepositoryImpl
import android.content.Context
import com.starnest.data.studio.datasource.dao.CategoryStudioDao
import com.starnest.data.studio.datasource.dao.DrawLayerDao
import com.starnest.data.studio.datasource.dao.DrawingDao
import com.starnest.data.studio.datasource.mapper.CategoryStudioMapper
import com.starnest.data.studio.datasource.mapper.DrawingItemMapper
import com.starnest.data.studio.datasource.mapper.DrawingLayerMapper
import com.starnest.data.studio.repository.CategoryStudioRepositoryImpl
import com.starnest.data.studio.repository.DrawLayerRepositoryImpl
import com.starnest.data.studio.repository.HistorySearchRepositoryImpl
import com.starnest.data.studio.shareprefs.HistorySearchPrefsDataSource
import com.starnest.data.studio.shareprefs.HistorySearchPrefsDataSourceImpl
import com.starnest.domain.studio.repository.CategoryStudioRepository
import com.starnest.domain.studio.repository.DrawLayerRepository
import com.starnest.domain.studio.repository.DrawingItemRepository
import com.starnest.domain.studio.repository.HistorySearchRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Singleton
    @Provides
    fun provideCategoryStudioRepository(
        mapper: CategoryStudioMapper,
        dao: CategoryStudioDao,
    ): CategoryStudioRepository {
        return CategoryStudioRepositoryImpl(
            dao,
            mapper
        )
    }

    @Singleton
    @Provides
    fun provideDrawItemRepository(
        mapper: DrawingItemMapper,
        dao: DrawingDao,
    ): DrawingItemRepository {
        return DrawItemRepositoryImpl(
            dao,
            mapper
        )
    }

    @Provides
    @Singleton
    fun provideHistorySearchPrefsDataSource(
        @ApplicationContext context: Context,
    ): HistorySearchPrefsDataSource = HistorySearchPrefsDataSourceImpl(context)


    @Provides
    @Singleton
    fun providerHistorySearchRepository(
        localDataSource: HistorySearchPrefsDataSource,
    ): HistorySearchRepository {
        return HistorySearchRepositoryImpl(localDataSource)
    }


    @Singleton
    @Provides
    fun provideDrawLayerRepository(
        mapper: DrawingLayerMapper,
        dao: DrawLayerDao,
    ): DrawLayerRepository {
        return DrawLayerRepositoryImpl(
            dao,
            mapper
        )
    }
}