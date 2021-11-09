package com.drunkenboys.calendarun.receiver

import android.app.AlarmManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.content.getSystemService
import com.drunkenboys.calendarun.data.schedule.entity.Schedule
import com.drunkenboys.calendarun.data.schedule.local.ScheduleLocalDataSource
import com.drunkenboys.calendarun.util.notificationDate
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class BootReceiver : BroadcastReceiver() {

    @Inject lateinit var scheduleDataSource: ScheduleLocalDataSource

    private val job = Job()
    private val coroutineScope = CoroutineScope(job)

    private val today = Calendar.getInstance()

    private lateinit var alarmManager: AlarmManager

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            alarmManager = context.getSystemService() ?: return
            setNotifications(context)
        } else {
            return
        }
    }

    private fun setNotifications(context: Context) {
        coroutineScope.launch {
            scheduleDataSource.fetchAllSchedule()
                .forEach { schedule -> setAlarmIfScheduleInFuture(schedule, context) }
        }
    }

    private fun setAlarmIfScheduleInFuture(schedule: Schedule, context: Context) {
        if (schedule.startDate.time > today.timeInMillis) {
            val triggerAtMillis = schedule.notificationDate()
            alarmManager.set(
                AlarmManager.RTC_WAKEUP,
                triggerAtMillis,
                ScheduleAlarmReceiver.createPendingIntent(context, schedule)
            )
        }
    }
}
