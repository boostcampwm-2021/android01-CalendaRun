package com.drunkenboys.calendarun.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.drunkenboys.calendarun.data.calendar.entity.Calendar
import com.drunkenboys.calendarun.data.calendar.local.CalendarDao
import com.drunkenboys.calendarun.data.calendartheme.entity.CalendarTheme
import com.drunkenboys.calendarun.data.calendartheme.local.CalendarThemeDao
import com.drunkenboys.calendarun.data.checkpoint.entity.CheckPoint
import com.drunkenboys.calendarun.data.checkpoint.local.CheckPointDao
import com.drunkenboys.calendarun.data.schedule.entity.Schedule
import com.drunkenboys.calendarun.data.schedule.local.ScheduleDao

@Database(entities = [Calendar::class, CheckPoint::class, Schedule::class, CalendarTheme::class], version = 2)
@TypeConverters(Converters::class)
abstract class Database : RoomDatabase() {

    abstract fun calendarDao(): CalendarDao

    abstract fun checkPointDao(): CheckPointDao

    abstract fun scheduleDao(): ScheduleDao

    abstract fun calendarThemeDao(): CalendarThemeDao

    companion object {

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    "CREATE TABLE `CalendarTheme` (" +
                            "`id` INTEGER NOT NULL, " +
                            "`weekDayTextColor` INTEGER NOT NULL, " +
                            "`holidayTextColor` INTEGER NOT NULL, " +
                            "`saturdayTextColor` INTEGER NOT NULL, " +
                            "`sundayTextColor` INTEGER NOT NULL, " +
                            "`selectedFrameColor` INTEGER NOT NULL, " +
                            "`backgroundColor` INTEGER NOT NULL, " +
                            "`textSize` INTEGER NOT NULL, " +
                            "`textAlign` INTEGER NOT NULL, " +
                            "`languageType` TEXT NOT NULL, " +
                            "`visibleScheduleCount` INTEGER NOT NULL, " +
                            "PRIMARY KEY(`id`))"
                )
            }
        }
    }
}
