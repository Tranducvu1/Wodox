package com.wodox.data.user.database

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import com.wodox.data.user.database.converter.DateConverter
import com.wodox.data.user.database.converter.UUIDConverter

@Module
@InstallIn(SingletonComponent::class)
object ConverterModule {
    @Provides
    fun provideUUIDConverter() = UUIDConverter()

    @Provides
    fun provideDateConverter() = DateConverter()
}
