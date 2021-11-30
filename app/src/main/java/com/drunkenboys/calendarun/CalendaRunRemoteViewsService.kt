package com.drunkenboys.calendarun

import android.content.Intent
import android.widget.RemoteViewsService
import com.drunkenboys.calendarun.data.schedule.local.ScheduleLocalDataSource
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class CalendaRunRemoteViewsService : RemoteViewsService() {

    @Inject
    lateinit var scheduleDataSource: ScheduleLocalDataSource

    override fun onGetViewFactory(p0: Intent?) = CalendaRunRemoteViewsFactory(this.applicationContext, scheduleDataSource)
}
