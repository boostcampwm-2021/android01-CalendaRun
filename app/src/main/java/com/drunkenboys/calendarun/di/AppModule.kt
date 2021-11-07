package com.drunkenboys.calendarun.di

import com.drunkenboys.calendarun.data.idstore.IdStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
object AppModule {

    @Provides
    @CalendarId
    fun provideCalendarId() = IdStore.getId(IdStore.KEY_CALENDAR_ID)

    @Provides
    @ScheduleId
    fun provideScheduleId() = IdStore.getId(IdStore.KEY_SCHEDULE_ID)
}
