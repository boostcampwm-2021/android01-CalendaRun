package com.drunkenboys.calendarun

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.core.content.getSystemService
import com.drunkenboys.calendarun.receiver.ScheduleAlarmReceiver
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App : Application() {

    override fun onCreate() {
        super.onCreate()
        prepareNotificationChannel()
    }

    private fun prepareNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = applicationContext.getSystemService<NotificationManager>() ?: return

            notificationManager.getNotificationChannel(ScheduleAlarmReceiver.SCHEDULE_NOTIFICATION_CHANNEL_ID) ?: run {
                val notificationChannel = NotificationChannel(
                    ScheduleAlarmReceiver.SCHEDULE_NOTIFICATION_CHANNEL_ID,
                    ScheduleAlarmReceiver.SCHEDULE_NOTIFICATION_CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH
                )

                notificationManager.createNotificationChannel(notificationChannel)
            }
        }
    }
}
