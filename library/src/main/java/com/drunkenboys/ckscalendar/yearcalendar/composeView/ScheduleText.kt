package com.drunkenboys.ckscalendar.yearcalendar.composeView

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
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
    viewModel.setWeekSchedules(weekScheduleList, today)

    weekScheduleList[today.dayOfWeek.dayValue()].forEach { schedule ->
        Text(
            text = if (schedule != null && (schedule.startDate.toLocalDate() == today || today.dayOfWeek == DayOfWeek.SUNDAY))
                schedule.text else " ",
            maxLines = 1,
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = if (schedule != null) Color(schedule.color) else Color.Transparent,
                    shape = when {
                        schedule != null && (schedule.endDate.toLocalDate() == today) ->
                            RoundedCornerShape(topEnd = 20.dp, bottomEnd = 20.dp)
                        schedule != null && (schedule.startDate.toLocalDate() == today) ->
                            RoundedCornerShape(topStart = 20.dp, bottomStart = 20.dp)
                        else ->
                            RectangleShape
                    }
                )
                .padding(4.dp),
            overflow = TextOverflow.Ellipsis,
            fontSize = viewModel.design.value.textSize.dp(),
            color = Color.White
        )
        Spacer(modifier = Modifier.height(2.dp))
    }
}