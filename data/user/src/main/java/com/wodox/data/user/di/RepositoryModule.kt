package com.wodox.data.user.di

import com.wodox.data.user.database.mapper.UserMapper
import com.wodox.data.user.repository.UserRepositoryImpl
import com.wodox.domain.user.repository.UserRepository
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
    fun provideUserRepository(
        mapper: UserMapper,
    ): UserRepository {
        return UserRepositoryImpl(
            mapper
        )
    }
}