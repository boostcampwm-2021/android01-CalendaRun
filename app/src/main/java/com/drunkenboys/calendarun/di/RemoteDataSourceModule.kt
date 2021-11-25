package com.drunkenboys.calendarun.di

import com.drunkenboys.calendarun.data.holiday.remote.HolidayDataSource
import com.drunkenboys.calendarun.data.holiday.remote.HolidayDataSourceImpl
import com.drunkenboys.calendarun.data.holiday.remote.HolidayService
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
    fun provideHolidayDataSource(holidayService: HolidayService): HolidayDataSource =
        HolidayDataSourceImpl(holidayService)
}
