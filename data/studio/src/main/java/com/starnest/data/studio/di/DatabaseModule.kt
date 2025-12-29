package com.starnest.data.studio.di

import android.content.Context
import com.starnest.data.studio.datasource.StudioDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Singleton
    @Provides
    fun provideChatDatabase(
        @ApplicationContext app: Context,
    ) = StudioDatabase.getDatabase(app)

    @Singleton
    @Provides
    fun provideCategoryDao(db: StudioDatabase) = db.categoryStudioDao()

    @Singleton
    @Provides
    fun provideDrawingItemDao(db: StudioDatabase) = db.drawingItemDao()

    @Singleton
    @Provides
    fun provideDrawingLayerDao(db: StudioDatabase) = db.drawingLayerDao()

}