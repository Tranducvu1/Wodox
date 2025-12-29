package com.wodox.data.remote.di

import com.wodox.data.remote.model.mapper.JsonSchemaMapper
import com.wodox.data.remote.model.mapper.ResponseFormatMapper
import com.wodox.data.remote.model.mapper.SchemaMapper
import com.wodox.data.remote.model.mapper.TextCompletionChoiceMapper
import com.wodox.data.remote.model.mapper.TextCompletionDataMapper
import com.wodox.data.remote.model.mapper.TextCompletionMessageMapper
import com.wodox.data.remote.model.mapper.TextCompletionMessageResponseMapper
import com.wodox.data.remote.model.mapper.TextCompletionRequestMapper
import com.wodox.data.remote.model.mapper.TextCompletionResponseMapper
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
    fun providerSchemaMapper(): SchemaMapper {
        return SchemaMapper()
    }

    @Singleton
    @Provides
    fun providerTextCompletionMessageMapper(): TextCompletionMessageMapper {
        return TextCompletionMessageMapper()
    }

    @Singleton
    @Provides
    fun providerJsonSchemaMapper(schemaMapper: SchemaMapper): JsonSchemaMapper {
        return JsonSchemaMapper(schemaMapper)
    }

    @Singleton
    @Provides
    fun providerTextCompletionDataMapper(mapper: TextCompletionChoiceMapper): TextCompletionDataMapper {
        return TextCompletionDataMapper(mapper)
    }

    @Singleton
    @Provides
    fun providerTextCompletionMessageResponseMapper(): TextCompletionMessageResponseMapper {
        return TextCompletionMessageResponseMapper()
    }

    @Singleton
    @Provides
    fun providerTextCompletionResponseMapper(mapper: TextCompletionDataMapper): TextCompletionResponseMapper {
        return TextCompletionResponseMapper(mapper)
    }

    @Singleton
    @Provides
    fun providerResponseFormatMapper(mapper: JsonSchemaMapper): ResponseFormatMapper {
        return ResponseFormatMapper(mapper)
    }

    @Singleton
    @Provides
    fun providerTextCompletionChoiceMapper(textCompletionMessageResponseMapper: TextCompletionMessageResponseMapper): TextCompletionChoiceMapper {
        return TextCompletionChoiceMapper(textCompletionMessageResponseMapper)
    }

    @Singleton
    @Provides
    fun providerTextCompletionRequestMapper(
        messageMapper: TextCompletionMessageMapper,
        responseFormatMapper: ResponseFormatMapper
    ): TextCompletionRequestMapper {
        return TextCompletionRequestMapper(messageMapper, responseFormatMapper)
    }
}