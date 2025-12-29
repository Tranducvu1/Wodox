package com.wodox.data.user.di


import com.wodox.data.user.database.mapper.UserMapper
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
    fun providerUserMapper(
    ): UserMapper {
        return UserMapper()
    }
}