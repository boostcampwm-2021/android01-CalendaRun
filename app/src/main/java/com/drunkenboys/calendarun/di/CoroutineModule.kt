package com.drunkenboys.calendarun.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers

@InstallIn(SingletonComponent::class)
@Module
object CoroutineModule {

    @Provides
    fun provideIODispatcher() = Dispatchers.IO
}
