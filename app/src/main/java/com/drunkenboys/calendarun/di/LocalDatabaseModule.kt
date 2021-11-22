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
        .addMigrations(Database.MIGRATION_2_3)
        .addMigrations(Database.MIGRATION_3_4)
        .build()

    @Provides
    fun provideCalendarDao(database: Database) = database.calendarDao()

    @Provides
    fun provideCheckPointDao(database: Database) = database.checkPointDao()

    @Provides
    fun provideScheduleDao(database: Database) = database.scheduleDao()

    @Provides
    fun provideCalendarThemeDao(database: Database) = database.calendarThemeDao()
}
