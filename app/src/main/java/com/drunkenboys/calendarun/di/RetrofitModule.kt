package com.drunkenboys.calendarun.di

import com.google.gson.GsonBuilder
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

    private val gson = GsonBuilder()
        .setLenient()
        .create()

    @Provides
    @Singleton
    fun provideHolidayRetrofit(): Retrofit =
        Retrofit.Builder()
            .baseUrl(HOLIDAY_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

}
