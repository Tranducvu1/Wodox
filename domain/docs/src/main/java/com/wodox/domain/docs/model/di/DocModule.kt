package com.wodox.domain.docs.model.di

import com.wodox.domain.docs.model.repository.SharedDocumentRepository
import com.wodox.domain.docs.model.usecase.DeleteSharedDocumentUseCase
import com.wodox.domain.docs.model.usecase.GetDocumentsByUserIdUseCase
import com.wodox.domain.docs.model.usecase.GetSharedDocumentByIdUseCase
import com.wodox.domain.docs.model.usecase.GetSharedDocumentsForUserUseCase
import com.wodox.domain.docs.model.usecase.SaveSharedDocumentUseCase
import com.wodox.domain.docs.model.usecase.UpdateSharedDocumentUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DocModule {

    @Provides
    @Singleton
    fun provideGetSharedDocumentsForUserUseCase(
        userRepository: SharedDocumentRepository,
    ): GetSharedDocumentsForUserUseCase {
        return GetSharedDocumentsForUserUseCase(userRepository)
    }

    @Provides
    @Singleton
    fun provideSaveSharedDocumentUseCase(
        repository: SharedDocumentRepository
    ): SaveSharedDocumentUseCase {
        return SaveSharedDocumentUseCase(repository)
    }
    @Provides
    @Singleton
    fun provideGetSharedDocumentByIdUseCase(
        repository: SharedDocumentRepository
    ): GetSharedDocumentByIdUseCase {
        return GetSharedDocumentByIdUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideUpdateSharedDocumentUseCase(
        repository: SharedDocumentRepository
    ): UpdateSharedDocumentUseCase {
       return UpdateSharedDocumentUseCase(repository)
    }
    @Provides
    @Singleton
    fun provideDeleteSharedDocumentUseCase(
        repository: SharedDocumentRepository
    ): DeleteSharedDocumentUseCase {
        return DeleteSharedDocumentUseCase(repository)
    }


    @Provides
    @Singleton
    fun provideGetDocumentsByUserIdUseCase(
        repository: SharedDocumentRepository
    ): GetDocumentsByUserIdUseCase {
        return GetDocumentsByUserIdUseCase(repository)
    }
}