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

@Database(entities = [Calendar::class, CheckPoint::class, Schedule::class, CalendarTheme::class], version = 3)
@TypeConverters(Converters::class)
abstract class Database : RoomDatabase() {

    abstract fun calendarDao(): CalendarDao

    abstract fun checkPointDao(): CheckPointDao

    abstract fun scheduleDao(): ScheduleDao

    abstract fun calendarThemeDao(): CalendarThemeDao

    companion object {

        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    """
                        CREATE TABLE new_calendar (
                            id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                            name TEXT NOT NULL
                        )
                    """.trimIndent()
                )
                database.execSQL(
                    """
                        INSERT INTO new_calendar (id, name)
                        SELECT id, name FROM calendar
                    """.trimIndent()
                )
                database.execSQL("DROP TABLE Calendar")
                database.execSQL("ALTER TABLE new_calendar RENAME TO Calendar")
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
