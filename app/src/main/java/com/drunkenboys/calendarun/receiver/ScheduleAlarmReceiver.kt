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

class ScheduleAlarmReceiver : BroadcastReceiver() {

    private lateinit var notificationManager: NotificationManager

    override fun onReceive(context: Context, intent: Intent) {
        notificationManager = context.getSystemService() ?: return

        val scheduleAlarmModel = intent.getParcelableExtra<ScheduleAlarmModel>(KEY_SCHEDULE_ALARM_MODEL) ?: return

        // fragment navigation은 어떻게?
        val contentIntent = Intent(context, MainActivity::class.java)
        val contentPendingIntent = PendingIntent.getActivity(
            context,
            NOTIFICATION_ID,
            contentIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        val builder = NotificationCompat.Builder(context, SCHEDULE_NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_calendar_today)
            .setContentTitle(scheduleAlarmModel.title)
            .setContentText(scheduleAlarmModel.content)
            .setContentIntent(contentPendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        notificationManager.notify(NOTIFICATION_ID, builder.build())
    }

    companion object {

        private const val KEY_SCHEDULE_ALARM_MODEL = "ScheduleAlarmModel"
        private const val NOTIFICATION_ID = 1000
        const val SCHEDULE_NOTIFICATION_CHANNEL_ID = "com.drunkenboys.calendarun.notification.schedule"
        const val SCHEDULE_NOTIFICATION_CHANNEL_NAME = "일정 알림 채널"
    }
}
