package com.drunkenboys.calendarun.di

import com.drunkenboys.calendarun.data.holiday.repository.HolidayRepository
import com.drunkenboys.calendarun.data.holiday.repository.HolidayRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
abstract class RepositoryModule {

    @Binds
    abstract fun bindHolidayRepository(repository: HolidayRepositoryImpl): HolidayRepository

}
