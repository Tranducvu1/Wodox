package com.wodox.domain.user.di

import com.wodox.domain.user.repository.UserRepository
import com.wodox.domain.user.usecase.GetAllUserUseCase
import com.wodox.domain.user.usecase.GetCurrentUser
import com.wodox.domain.user.usecase.GetCurrentUserEmail
import com.wodox.domain.user.usecase.GetUserByEmailUseCase
import com.wodox.domain.user.usecase.GetUserById
import com.wodox.domain.user.usecase.GetUserUseCase
import com.wodox.domain.user.usecase.SaveUserUseCase
import com.wodox.domain.user.usecase.SignOutUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object UserModule {

    @Provides
    @Singleton
    fun provideSaveUserUseCase(
        userRepository: UserRepository,
    ): SaveUserUseCase {
        return SaveUserUseCase(userRepository)
    }

    @Provides
    @Singleton
    fun provideGetAllUserUseCase(
        userRepository: UserRepository,
    ): GetAllUserUseCase {
        return GetAllUserUseCase(userRepository)
    }

    @Provides
    @Singleton
    fun provideGetUserByEmailUseCase(
        userRepository: UserRepository,
    ): GetUserByEmailUseCase {
        return GetUserByEmailUseCase(userRepository)
    }

    @Singleton
    @Provides
    fun provideGetUserUseCase(
        userRepository: UserRepository,
    ): GetUserUseCase {
        return GetUserUseCase(userRepository)
    }

    @Singleton
    @Provides
    fun provideGetCurrentUserEmail(
        userRepository: UserRepository,
    ): GetCurrentUserEmail {
        return GetCurrentUserEmail(userRepository)
    }

    @Singleton
    @Provides
    fun provideSingoutUseCase(
        userRepository: UserRepository,
    ): SignOutUseCase {
        return SignOutUseCase(userRepository)
    }

    @Singleton
    @Provides
    fun provideGetUserById(
        userRepository: UserRepository,
    ): GetUserById {
        return GetUserById(userRepository)
    }


    @Singleton
    @Provides
    fun provideGetCurrentUser(
        userRepository: UserRepository,
    ): GetCurrentUser {
        return GetCurrentUser(userRepository)
    }
}

