package com.drunkenboys.calendarun.di

import com.drunkenboys.calendarun.data.holiday.remote.HolidayService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object HolidayRetrofitServiceModule {

    @Provides
    @Singleton
    fun provideHolidayService(retrofit: Retrofit): HolidayService =
        retrofit.create(HolidayService::class.java)
}
