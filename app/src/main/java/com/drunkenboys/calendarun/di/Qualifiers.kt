package com.drunkenboys.calendarun.di

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class CalendarId

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ScheduleId
