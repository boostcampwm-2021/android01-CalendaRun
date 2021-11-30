package com.drunkenboys.ckscalendar.yearcalendar.composeView

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.drunkenboys.ckscalendar.data.CalendarScheduleObject
import com.drunkenboys.ckscalendar.utils.TimeUtils
import com.drunkenboys.ckscalendar.utils.TimeUtils.dayValue
import com.drunkenboys.ckscalendar.utils.dp
import com.drunkenboys.ckscalendar.yearcalendar.YearCalendarViewModel
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime

@Composable
fun ScheduleText(
    today: LocalDate,
    weekScheduleList: Array<Array<CalendarScheduleObject?>>,
    viewModel: YearCalendarViewModel
) {
    viewModel.setWeekSchedules(weekScheduleList, today)

    weekScheduleList[today.dayOfWeek.dayValue()].forEach { schedule ->
        Row {
            if (schedule != null && (schedule.startDate.toLocalDate() == today)) Spacer(modifier = Modifier.width(10.dp))

            Text(
                text = if (schedule != null && (schedule.startDate.toLocalDate() == today || today.dayOfWeek == DayOfWeek.SUNDAY))
                    schedule.text else " ",
                maxLines = 1,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = if (schedule != null) Color(schedule.color) else Color.Transparent)
                    .padding(4.dp),
                overflow = TextOverflow.Ellipsis,
                fontSize = viewModel.design.value.textSize.dp(),
                color = Color.White
            )
        }
        Spacer(modifier = Modifier.height(2.dp))
    }
}

@Preview
@Composable
fun PreviewSchedule() {
    val viewModel = YearCalendarViewModel()
    ScheduleText(
        today = LocalDate.now(),
        weekScheduleList =  Array(7) { Array(viewModel.design.value.visibleScheduleCount) { CalendarScheduleObject(
            id = 1,
            color = TimeUtils.getColorInt(255, 0, 0),
            text = "test",
            startDate = LocalDateTime.now(),
            endDate = LocalDateTime.now()
        ) } },
        viewModel = viewModel
    )
}