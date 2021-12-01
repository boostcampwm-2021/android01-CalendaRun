package com.drunkenboys.calendarun.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RetrofitModule {

    private const val HOLIDAY_URL = "http://apis.data.go.kr"

    @Provides
    @Singleton
    fun provideHolidayRetrofit(): Retrofit =
        Retrofit.Builder()
            .baseUrl(HOLIDAY_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

}
