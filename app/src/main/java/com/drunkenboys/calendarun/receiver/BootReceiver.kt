package com.drunkenboys.calendarun.receiver

import android.app.AlarmManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.content.getSystemService
import com.drunkenboys.calendarun.data.calendar.local.CalendarLocalDataSource
import com.drunkenboys.calendarun.data.schedule.entity.Schedule
import com.drunkenboys.calendarun.data.schedule.local.ScheduleLocalDataSource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

@AndroidEntryPoint
class BootReceiver : BroadcastReceiver() {

    @Inject lateinit var calendarDataSource: CalendarLocalDataSource
    @Inject lateinit var scheduleDataSource: ScheduleLocalDataSource

    private val job = Job()
    private val coroutineScope = CoroutineScope(job)

    private val today = LocalDateTime.now()

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
            val calendarMap = calendarDataSource.fetchAllCalendar()
                .associate { it.id to it.name }

            scheduleDataSource.fetchAllSchedule()
                .forEach { schedule ->
                    setAlarmIfScheduleInFuture(
                        schedule,
                        context,
                        calendarMap.getOrDefault(schedule.id, "null")
                    )
                }
        }
    }

    private fun setAlarmIfScheduleInFuture(schedule: Schedule, context: Context, calendarName: String) {
        if (schedule.startDate > today) {
            val triggerAtMillis = schedule.notificationDateTimeMillis()
            alarmManager.set(
                AlarmManager.RTC_WAKEUP,
                triggerAtMillis,
                ScheduleAlarmReceiver.createPendingIntent(context, schedule, calendarName)
            )
        }
    }
}
