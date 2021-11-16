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
import com.drunkenboys.ckscalendar.data.CalendarDesignObject
import com.drunkenboys.ckscalendar.data.CalendarScheduleObject
import com.drunkenboys.ckscalendar.utils.TimeUtils.dayValue
import com.drunkenboys.ckscalendar.utils.TimeUtils.isSameWeek
import com.drunkenboys.ckscalendar.utils.dp
import com.drunkenboys.ckscalendar.yearcalendar.CalendarState
import java.time.DayOfWeek
import java.time.LocalDate

@Composable
fun ScheduleText(
    today: LocalDate,
    weekScheduleList: Array<Array<CalendarScheduleObject?>>,
    design: CalendarDesignObject,
    calendarState: CalendarState
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

    setWeekSchedule(getStartScheduleList(today, calendarState.schedules.value), weekScheduleList, today)

    weekScheduleList[weekNum].forEach { schedule ->
        Text(
            text = scheduleText(schedule),
            maxLines = 1,
            modifier = Modifier
                .fillMaxWidth()
                .background(color = color(schedule)),
            overflow = TextOverflow.Ellipsis,
            fontSize = design.textSize.dp(),
            color = Color.White
        )
        Spacer(modifier = Modifier.height(2.dp))
    }
}

private fun setWeekSchedule(
    todaySchedules: List<CalendarScheduleObject>?,
    weekSchedules: Array<Array<CalendarScheduleObject?>>,
    today: LocalDate
) {
    val todayOfWeek = today.dayOfWeek.dayValue()

    todaySchedules?.forEach { todaySchedule ->
        val weekEndDate =
            if (!today.isSameWeek(todaySchedule.endDate.toLocalDate())) DayOfWeek.SATURDAY.value
            else todaySchedule.endDate.dayOfWeek.dayValue()

        weekSchedules[todayOfWeek].forEachIndexed { index, space ->
            if (space == null) {
                (todayOfWeek..weekEndDate).forEach { weekNum ->
                    weekSchedules[weekNum][index] = todaySchedule
                }
                return@forEach
            }
        }
    }
}

private fun getStartScheduleList(today: LocalDate, schedules: List<CalendarScheduleObject>) = schedules.filter { schedule ->
    // TODO: 정렬
    val isStart = schedule.startDate.toLocalDate() == today
    val isSunday = today.dayOfWeek == DayOfWeek.SUNDAY
    val isFirstOfMonth = today.dayOfMonth == 1
    val isDateInScheduleRange = today in schedule.startDate.toLocalDate()..schedule.endDate.toLocalDate()
    isStart || ((isSunday || isFirstOfMonth) && (isDateInScheduleRange))
}