package com.drunkenboys.calendarun

import android.content.Context
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import com.drunkenboys.calendarun.data.schedule.local.ScheduleLocalDataSource
import com.drunkenboys.calendarun.ui.searchschedule.model.ScheduleItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class CalendaRunRemoteViewsFactory constructor(
    private val context: Context,
    private val scheduleDataSource: ScheduleLocalDataSource
) : RemoteViewsService.RemoteViewsFactory {

    private val job = SupervisorJob()
    private val coroutineScope = CoroutineScope(Dispatchers.IO + job)

    private var scheduleList: List<ScheduleItem> = listOf()

    override fun onCreate() {
        setDate()
    }

    override fun onDataSetChanged() {
        setDate()
    }

    private fun setDate() {
        coroutineScope.launch {
            scheduleDataSource.fetchDateSchedule(LocalDateTime.now()).collectLatest { newScheduleList ->
                scheduleList = newScheduleList.map { schedule ->
                    ScheduleItem(schedule) {}
                }
            }
        }
    }

    override fun onDestroy() {}

    override fun getCount() = scheduleList.size

    override fun getViewAt(position: Int): RemoteViews {
        val widget = RemoteViews(context.packageName, R.layout.item_app_widget_schedule).apply {
            setTextViewText(R.id.tv_appWidget_schedule_name, scheduleList[position].schedule.name)
            setTextViewText(R.id.tv_appWidget_schedule_time, scheduleList[position].schedule.startDate.toString())
        }

        /*val dataIntent = Intent()
        dataIntent.putExtra("item_id", scheduleList[position].schedule.calendarId)
        dataIntent.putExtra("item_name", scheduleList[position].schedule.name)
        dataIntent.putExtra("item_time", scheduleList[position].schedule.startDate.toString())
        widget.setOnClickFillInIntent(R.id.layout_appWidget_schedule, dataIntent)
        //setOnClickFillInIntent 브로드캐스트 리시버에서 항목 선택 이벤트가 발생할 때 실행을 의뢰한 인텐트에 각 항목의 데이터를 추가해주는 함수
        //브로드캐스트 리시버의 인텐트와 Extra 데이터가 담긴 인텐트를 합치는 역할을 한다.*/

        return widget
    }

    override fun getLoadingView(): RemoteViews? {
        // TODO: 2021-11-29 로딩 뷰 추가
        return null
    }

    override fun getViewTypeCount() = VIEW_TYPE_COUNT

    override fun getItemId(position: Int) = 0L

    override fun hasStableIds() = false

    companion object {
        private const val VIEW_TYPE_COUNT = 1
    }
}
