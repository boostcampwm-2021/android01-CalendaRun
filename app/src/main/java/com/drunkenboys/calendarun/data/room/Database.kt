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
import com.drunkenboys.calendarun.data.holiday.entity.Holiday
import com.drunkenboys.calendarun.data.holiday.local.HolidayDao
import com.drunkenboys.calendarun.data.schedule.entity.Schedule
import com.drunkenboys.calendarun.data.schedule.local.ScheduleDao

@Database(entities = [Calendar::class, CheckPoint::class, Schedule::class, CalendarTheme::class, Holiday::class], version = 5)
@TypeConverters(Converters::class)
abstract class Database : RoomDatabase() {

    abstract fun calendarDao(): CalendarDao

    abstract fun checkPointDao(): CheckPointDao

    abstract fun scheduleDao(): ScheduleDao

    abstract fun calendarThemeDao(): CalendarThemeDao

    abstract fun holidayDao(): HolidayDao

    companion object {

        val MIGRATION_4_5 = object : Migration(4, 5) {
            // TODO: 2021-11-30 migration 코드 짜기
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    """
                        CREATE TABLE Holiday (
                            id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                            name TEXT NOT NULL,
                            date INTEGER NOT NULL
                        )
                    """.trimIndent()
                )
            }
        }

        // CalendarTheme TextSize 데이터 타입 변경 Int -> Float
        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    "CREATE TABLE `CalendarTheme_tmp` (" +
                            "`id` INTEGER NOT NULL, " +
                            "`weekDayTextColor` INTEGER NOT NULL, " +
                            "`holidayTextColor` INTEGER NOT NULL, " +
                            "`saturdayTextColor` INTEGER NOT NULL, " +
                            "`sundayTextColor` INTEGER NOT NULL, " +
                            "`selectedFrameColor` INTEGER NOT NULL, " +
                            "`backgroundColor` INTEGER NOT NULL, " +
                            "`textSize` FlOAT NOT NULL, " +
                            "`textAlign` INTEGER NOT NULL, " +
                            "`languageType` TEXT NOT NULL, " +
                            "`visibleScheduleCount` INTEGER NOT NULL, " +
                            "PRIMARY KEY(`id`))"
                )
                database.execSQL(
                    """INSERT INTO CalendarTheme_tmp(
                         id,
                         weekDayTextColor,
                         holidayTextColor,
                         saturdayTextColor,
                         sundayTextColor,
                         selectedFrameColor,
                         backgroundColor,
                         textSize,
                         textAlign,
                         languageType,
                         visibleScheduleCount
                        ) 
                        SELECT 
                             id,
                             weekDayTextColor,
                             holidayTextColor,
                             saturdayTextColor,
                             sundayTextColor,
                             selectedFrameColor,
                             backgroundColor,
                             textSize,
                             textAlign,
                             languageType,
                             visibleScheduleCount 
                       FROM CalendarTheme
                           """.trimMargin()
                )
                database.execSQL("DROP TABLE CalendarTheme");
                database.execSQL("ALTER TABLE CalendarTheme_tmp RENAME TO CalendarTheme");
            }
        }

        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    """
                        CREATE TABLE new_checkpoint (
                            id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                            calendarId INTEGER NOT NULL,
                            name TEXT NOT NULL,
                            startDate INTEGER NOT NULL,
                            endDate INTEGER NOT NULL,
                            CONSTRAINT calendarId FOREIGN KEY(calendarId) REFERENCES Calendar(id) ON DELETE CASCADE ON UPDATE CASCADE
                        )
                    """.trimIndent()
                )
                database.execSQL(
                    """
                        INSERT INTO new_checkpoint (id, calendarId, name, startDate, endDate)
                        SELECT id, calendarId, name, date, date FROM Checkpoint
                    """.trimIndent()
                )
                database.execSQL("DROP TABLE CheckPoint")
                database.execSQL("ALTER TABLE new_checkpoint RENAME TO Checkpoint")
            }
        }

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
