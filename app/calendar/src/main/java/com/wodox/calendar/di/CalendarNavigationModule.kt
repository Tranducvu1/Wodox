package com.wodox.calendar.di


import com.wodox.calendar.navigation.CalenderNavigationImpl
import com.wodox.common.navigation.AuthNavigator
import com.wodox.common.navigation.CalendarNavigator
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CalendarNavigationModule {
    @Provides
    @Singleton
    fun provideCalendarNavigator(): CalendarNavigator = CalenderNavigationImpl()
}