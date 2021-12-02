package com.drunkenboys.calendarun.di

import android.content.Context
import androidx.room.Room
import com.drunkenboys.calendarun.data.room.Database
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object LocalDatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext appContext: Context) = Room.databaseBuilder(
        appContext,
        Database::class.java,
        "AppDatabase.db"
    )
        .createFromAsset("default.db")
        .addMigrations(Database.MIGRATION_2_3, Database.MIGRATION_3_4, Database.MIGRATION_4_5, Database.MIGRATION_5_6)
        .build()

    @Provides
    fun provideCalendarDao(database: Database) = database.calendarDao()

    @Provides
    fun provideSliceDao(database: Database) = database.sliceDao()

    @Provides
    fun provideScheduleDao(database: Database) = database.scheduleDao()

    @Provides
    fun provideCalendarThemeDao(database: Database) = database.calendarThemeDao()

    @Provides
    fun provideHolidayDao(database: Database) = database.holidayDao()
}
