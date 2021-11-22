package com.drunkenboys.calendarun.receiver

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.content.getSystemService
import com.drunkenboys.calendarun.MainActivity
import com.drunkenboys.calendarun.R
import com.drunkenboys.calendarun.data.schedule.entity.Schedule
import com.drunkenboys.calendarun.util.extensions.PendingIntentExt

class ScheduleAlarmReceiver : BroadcastReceiver() {

    private lateinit var notificationManager: NotificationManager

    override fun onReceive(context: Context, intent: Intent) {
        notificationManager = context.getSystemService() ?: return
        val title = intent.getStringExtra(KEY_SCHEDULE_NOTIFICATION_TITLE) ?: return
        val text = intent.getStringExtra(KEY_SCHEDULE_NOTIFICATION_TEXT) ?: return
        val calendarId = intent.getLongExtra(KEY_SCHEDULE_NOTIFICATION_CALENDAR_ID, 0)
        val scheduleId = intent.getLongExtra(KEY_SCHEDULE_NOTIFICATION_SCHEDULE_ID, 0)

        val contentPendingIntent = MainActivity.createNavigationPendingIntent(context, calendarId, scheduleId)

        val builder = NotificationCompat.Builder(context, SCHEDULE_NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_calendar_today)
            .setContentTitle(title)
            .setContentText(text)
            .setContentIntent(contentPendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        notificationManager.notify(scheduleId.toInt(), builder.build())
    }

    companion object {

        const val KEY_SCHEDULE_NOTIFICATION_TITLE = "title"
        const val KEY_SCHEDULE_NOTIFICATION_TEXT = "text"
        const val KEY_SCHEDULE_NOTIFICATION_CALENDAR_ID = "calendarId"
        const val KEY_SCHEDULE_NOTIFICATION_SCHEDULE_ID = "scheduleId"
        const val SCHEDULE_NOTIFICATION_CHANNEL_ID = "com.drunkenboys.calendarun.notification.schedule"
        const val SCHEDULE_NOTIFICATION_CHANNEL_NAME = "일정 알림 채널"

        fun createPendingIntent(context: Context, schedule: Schedule): PendingIntent {
            val intent = Intent(context, ScheduleAlarmReceiver::class.java).apply {
                putExtra(KEY_SCHEDULE_NOTIFICATION_TEXT, schedule.name)
                putExtra(KEY_SCHEDULE_NOTIFICATION_TITLE, schedule.memo)
                putExtra(KEY_SCHEDULE_NOTIFICATION_CALENDAR_ID, schedule.calendarId)
                putExtra(KEY_SCHEDULE_NOTIFICATION_SCHEDULE_ID, schedule.id)
            }

            return PendingIntent.getBroadcast(
                context,
                schedule.id.toInt(),
                intent,
                PendingIntentExt.FLAG_UPDATE_CURRENT
            )
        }
    }
}
