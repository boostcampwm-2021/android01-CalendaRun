package com.drunkenboys.calendarun.di

import com.drunkenboys.calendarun.data.calendar.local.CalendarLocalDataSource
import com.drunkenboys.calendarun.data.calendar.local.CalendarLocalDataSourceImpl
import com.drunkenboys.calendarun.data.calendartheme.local.CalendarThemeLocalDataSource
import com.drunkenboys.calendarun.data.calendartheme.local.CalendarThemeLocalDataSourceImpl
import com.drunkenboys.calendarun.data.checkpoint.local.CheckPointLocalDataSource
import com.drunkenboys.calendarun.data.checkpoint.local.CheckPointLocalDataSourceImpl
import com.drunkenboys.calendarun.data.holiday.local.HolidayLocalDataSource
import com.drunkenboys.calendarun.data.holiday.local.HolidayLocalDataSourceImpl
import com.drunkenboys.calendarun.data.holiday.remote.HolidayRemoteDataSource
import com.drunkenboys.calendarun.data.holiday.remote.HolidayRemoteDataSourceImpl
import com.drunkenboys.calendarun.data.schedule.local.ScheduleLocalDataSource
import com.drunkenboys.calendarun.data.schedule.local.ScheduleLocalDataSourceImpl
import com.drunkenboys.calendarun.data.slice.local.SliceLocalDataSource
import com.drunkenboys.calendarun.data.slice.local.SliceLocalDataSourceImpl
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
    abstract fun bindSliceDataSource(dataSource: SliceLocalDataSourceImpl): SliceLocalDataSource

    @Binds
    abstract fun bindScheduleDataSource(dataSource: ScheduleLocalDataSourceImpl): ScheduleLocalDataSource

    @Binds
    abstract fun bindCalendarThemeDataSource(dataSource: CalendarThemeLocalDataSourceImpl): CalendarThemeLocalDataSource

    @Binds
    abstract fun bindHolidayLocalDataSource(dataSource: HolidayLocalDataSourceImpl): HolidayLocalDataSource

    @Binds
    abstract fun bindHolidayRemoteDataSource(dataSource: HolidayRemoteDataSourceImpl): HolidayRemoteDataSource
}
