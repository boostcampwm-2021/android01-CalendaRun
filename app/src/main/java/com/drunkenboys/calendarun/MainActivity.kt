package com.drunkenboys.calendarun

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import com.drunkenboys.calendarun.databinding.ActivityMainBinding
import com.drunkenboys.calendarun.receiver.ScheduleAlarmReceiver
import com.drunkenboys.calendarun.ui.base.BaseViewActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseViewActivity<ActivityMainBinding>(ActivityMainBinding::inflate) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    // TODO: 2021-11-09 알림 클릭 시 Fragment 내비게이션 구현

    companion object {

        fun createNavigationPendingIntent(context: Context, calendarId: Int, scheduleId: Int): PendingIntent {
            val contentIntent = Intent(context, MainActivity::class.java).apply {
                putExtra(ScheduleAlarmReceiver.KEY_SCHEDULE_NOTIFICATION_CALENDAR_ID, calendarId)
                putExtra(ScheduleAlarmReceiver.KEY_SCHEDULE_NOTIFICATION_SCHEDULE_ID, scheduleId)
            }

            val pendingIntentFlags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
            else
                PendingIntent.FLAG_UPDATE_CURRENT
            return PendingIntent.getActivity(
                context,
                ScheduleAlarmReceiver.NOTIFICATION_ID,
                contentIntent,
                pendingIntentFlags
            )
        }
    }
}
