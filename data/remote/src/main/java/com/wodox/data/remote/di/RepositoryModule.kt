package com.wodox.data.remote.di

import android.app.Application
import com.wodox.data.remote.model.datasource.AIDataSource
import com.wodox.data.remote.model.mapper.TextCompletionRequestMapper
import com.wodox.data.remote.model.mapper.TextCompletionResponseMapper
import com.wodox.data.remote.repository.AIChatRepositoryImpl
import com.wodox.domain.remote.repository.AIChatRepository
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
    fun provideAIChatRepository(
        app: Application,
        aiChatDataSource: AIDataSource,
        textCompletionResponseMapper: TextCompletionResponseMapper,
        textCompletionRequestMapper: TextCompletionRequestMapper,
    ): AIChatRepository {
        return AIChatRepositoryImpl(
            app,
            aiChatDataSource = aiChatDataSource,
            textCompletionResponseMapper = textCompletionResponseMapper,
            textCompletionRequestMapper = textCompletionRequestMapper,
        )
    }
}