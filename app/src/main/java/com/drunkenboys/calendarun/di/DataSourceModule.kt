package com.drunkenboys.calendarun.di

import com.drunkenboys.calendarun.data.calendar.local.CalendarLocalDataSource
import com.drunkenboys.calendarun.data.calendar.local.CalendarLocalDataSourceImpl
import com.drunkenboys.calendarun.data.checkpoint.local.CheckPointLocalDataSource
import com.drunkenboys.calendarun.data.checkpoint.local.CheckPointLocalDataSourceImpl
import com.drunkenboys.calendarun.data.schedule.local.ScheduleLocalDataSource
import com.drunkenboys.calendarun.data.schedule.local.ScheduleLocalDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
abstract class DataSourceModule {

    @Binds
    abstract fun bindCalendarDataSource(dataSource: CalendarLocalDataSourceImpl): CalendarLocalDataSource

    @Binds
    abstract fun bindCheckPointDataSource(dataSource: CheckPointLocalDataSourceImpl): CheckPointLocalDataSource

    @Binds
    abstract fun bindScheduleDataSource(dataSource: ScheduleLocalDataSourceImpl): ScheduleLocalDataSource

}
