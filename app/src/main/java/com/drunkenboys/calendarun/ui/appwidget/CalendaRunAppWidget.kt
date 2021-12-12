package com.drunkenboys.calendarun.ui.appwidget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetManager.ACTION_APPWIDGET_UPDATE
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.drunkenboys.calendarun.R
import com.drunkenboys.calendarun.util.localDateToString
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate

class CalendaRunAppWidget : AppWidgetProvider() {

    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        val remoteViews = RemoteViews(context.packageName, R.layout.calendarun_app_widget)

        val widgetText = LocalDate.now().localDateToString()
        remoteViews.setTextViewText(R.id.tv_appWidget_date, widgetText)

        val serviceIntent = Intent(context, CalendaRunRemoteViewsService::class.java)
        remoteViews.setRemoteAdapter(R.id.lv_appWidget_scheduleList, serviceIntent)

        val refreshIntent = getUpdateIntent(context)

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

        if (action.equals(ACTION_APPWIDGET_UPDATE)) {
            coroutineScope.launch {
                val appWidgetManager = AppWidgetManager.getInstance(context)
                val appWidget = ComponentName(context.packageName, CalendaRunAppWidget::class.java.name)
                val appWidgetIds = appWidgetManager.getAppWidgetIds(appWidget)

                onUpdate(context, appWidgetManager, appWidgetIds)
            }
        }
    }

    companion object {
        private fun getUpdateIntent(context: Context) = Intent(context, CalendaRunAppWidget::class.java)
            .setAction(ACTION_APPWIDGET_UPDATE)

        fun sendUpdateBroadcast(context: Context) {
            context.sendBroadcast(getUpdateIntent(context))
        }
    }
}

