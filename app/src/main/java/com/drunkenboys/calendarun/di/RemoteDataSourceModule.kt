package com.drunkenboys.calendarun.di

import com.drunkenboys.calendarun.data.holiday.remote.HolidayRemoteDataSource
import com.drunkenboys.calendarun.data.holiday.remote.HolidayRemoteDataSourceImpl
import com.drunkenboys.calendarun.data.holiday.remote.HolidayRemoteService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RemoteDataSourceModule {

    @Provides
    @Singleton
    fun provideHolidayRemoteDataSource(holidayRemoteService: HolidayRemoteService): HolidayRemoteDataSource =
        HolidayRemoteDataSourceImpl(holidayRemoteService)
}
