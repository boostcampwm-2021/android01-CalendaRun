package com.drunkenboys.calendarun.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.drunkenboys.calendarun.data.calendar.entity.Calendar
import com.drunkenboys.calendarun.data.calendar.local.CalendarDao
import com.drunkenboys.calendarun.data.checkpoint.entity.CheckPoint
import com.drunkenboys.calendarun.data.checkpoint.local.CheckPointDao
import com.drunkenboys.calendarun.data.schedule.entity.Schedule
import com.drunkenboys.calendarun.data.schedule.local.ScheduleDao

@Database(entities = [Calendar::class, CheckPoint::class, Schedule::class], version = 1)
abstract class Database : RoomDatabase() {

    abstract fun calendarDao(): CalendarDao

    abstract fun checkPointDao(): CheckPointDao

    abstract fun scheduleDao(): ScheduleDao

}
