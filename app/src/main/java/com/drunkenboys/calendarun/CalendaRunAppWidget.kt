package com.drunkenboys.calendarun

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.drunkenboys.calendarun.util.localDateToString
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.time.LocalDate

class CalendaRunAppWidget : AppWidgetProvider() {

    private val job = SupervisorJob()
    private val coroutineScope = CoroutineScope(Dispatchers.Main + job)

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        val remoteViews = RemoteViews(context.packageName, R.layout.calendarun_app_widget)

        val widgetText = LocalDate.now().localDateToString()
        remoteViews.setTextViewText(R.id.tv_appWidget_date, widgetText)

        val serviceIntent = Intent(context, CalendaRunRemoteViewsService::class.java)
        remoteViews.setRemoteAdapter(R.id.lv_appWidget_scheduleList, serviceIntent)

        val refreshIntent = Intent(context, CalendaRunAppWidget::class.java)
            .setAction(context.getString(R.string.action_refresh_click))
            .putExtra(APPWIDGET_ID, AppWidgetManager.EXTRA_APPWIDGET_ID)

        val pendingIntent = PendingIntent.getBroadcast(context, 0, refreshIntent, 0)
        remoteViews.setOnClickPendingIntent(R.id.iv_appWidget_refresh, pendingIntent)

        coroutineScope.launch {
            appWidgetManager.updateAppWidget(appWidgetIds, remoteViews)
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.lv_appWidget_scheduleList)
        }

        super.onUpdate(context, appWidgetManager, appWidgetIds)
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)

        val action = intent.action

        if (action.equals(context.getString(R.string.action_refresh_click))) {
            coroutineScope.launch {
                val appWidgetManager =
                    AppWidgetManager.getInstance(context)
                val appWidget = ComponentName(
                    context.packageName,
                    CalendaRunAppWidget::class.java.name
                )
                val appWidgetIds = appWidgetManager.getAppWidgetIds(appWidget)
                appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.lv_appWidget_scheduleList)
            }
        }
    }

    companion object {
        private const val APPWIDGET_ID = "id"
    }
}
