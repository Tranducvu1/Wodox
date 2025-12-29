package com.wodox.data.docs.di

import android.content.Context
import com.wodox.data.docs.repository.SharedDocumentMapper
import com.wodox.data.docs.repository.SharedDocumentRepositoryImpl
import com.wodox.domain.docs.model.repository.SharedDocumentRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideSharedDocumentRepository(
        @ApplicationContext context: Context,
        mapper: SharedDocumentMapper
    ): SharedDocumentRepository {
        return SharedDocumentRepositoryImpl(context,mapper)
    }
}
