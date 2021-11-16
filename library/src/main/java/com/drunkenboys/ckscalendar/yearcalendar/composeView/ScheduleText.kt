package com.drunkenboys.ckscalendar.yearcalendar.composeView

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.drunkenboys.ckscalendar.data.CalendarScheduleObject
import com.drunkenboys.ckscalendar.utils.TimeUtils.dayValue
import com.drunkenboys.ckscalendar.utils.dp
import com.drunkenboys.ckscalendar.yearcalendar.YearCalendarViewModel
import java.time.DayOfWeek
import java.time.LocalDate

@Composable
fun ScheduleText(
    today: LocalDate,
    weekScheduleList: Array<Array<CalendarScheduleObject?>>,
    viewModel: YearCalendarViewModel
) {
    val weekNum = (today.dayOfWeek.dayValue())

    val scheduleText = { schedule: CalendarScheduleObject? ->
        when {
            schedule == null -> " "
            schedule.startDate.toLocalDate() == today || today.dayOfWeek == DayOfWeek.SUNDAY -> schedule.text
            else -> " "
        }
    }

    val color = { schedule: CalendarScheduleObject? ->
        if (schedule != null) Color(schedule.color) else Color.Transparent
    }

    viewModel.setWeekSchedules(weekScheduleList, today)

    weekScheduleList[weekNum].forEach { schedule ->
        Text(
            text = scheduleText(schedule),
            maxLines = 1,
            modifier = Modifier
                .fillMaxWidth()
                .background(color = color(schedule)),
            overflow = TextOverflow.Ellipsis,
            fontSize = viewModel.design.value.textSize.dp(),
            color = Color.White
        )
        Spacer(modifier = Modifier.height(2.dp))
    }
}