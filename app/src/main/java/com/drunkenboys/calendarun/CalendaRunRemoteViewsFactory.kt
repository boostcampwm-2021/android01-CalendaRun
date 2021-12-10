package com.drunkenboys.calendarun

import android.content.Context
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import com.drunkenboys.calendarun.data.schedule.local.ScheduleLocalDataSource
import com.drunkenboys.calendarun.util.getEndOfDate
import com.drunkenboys.calendarun.util.getStartOfDate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CalendaRunRemoteViewsFactory constructor(
    private val context: Context,
    private val scheduleDataSource: ScheduleLocalDataSource
) : RemoteViewsService.RemoteViewsFactory {

    private val coroutineScope = CoroutineScope(Dispatchers.IO)
    private var scheduleList = scheduleDataSource.fetchDateSchedule(
        getStartOfDate(), getEndOfDate()
    ).stateIn(coroutineScope, SharingStarted.Eagerly, emptyList())

    override fun onCreate() {
        coroutineScope.launch {
            scheduleList.collectLatest {
                CalendaRunAppWidget.sendUpdateBroadcast(context)
            }
        }
    }

    override fun onDataSetChanged() {}

    override fun onDestroy() {}

    override fun getCount() = scheduleList.value.size

    override fun getViewAt(position: Int): RemoteViews {
        val widget = RemoteViews(context.packageName, R.layout.item_app_widget_schedule).apply {
            setTextViewText(R.id.tv_appWidget_schedule_name, scheduleList.value[position].name)
            setTextViewText(R.id.tv_appWidget_schedule_memo, scheduleList.value[position].memo)
        }

        return widget
    }

    override fun getLoadingView(): RemoteViews? = null

    override fun getViewTypeCount() = 1

    override fun getItemId(position: Int) = 0L

    override fun hasStableIds() = false
}
