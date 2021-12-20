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

/**
 * 일정을 보여준다.
 */
@Composable
fun ScheduleText(
    today: LocalDate,
    weekScheduleList: Array<Array<CalendarScheduleObject?>>,
    viewModel: YearCalendarViewModel
) {
    // 7 * visibleCount 만큼의 배열을 관리
    viewModel.setWeekSchedules(weekScheduleList, today)

    weekScheduleList[today.dayOfWeek.dayValue()].forEach { schedule ->
        // 일정의 시작일에 2dp의 패딩으로 시작 표시를 해주기 위해 Row를 사용.
        Row {
            // 일정 시작일 패딩
            if (schedule != null && (schedule.startDate.toLocalDate() == today)) Spacer(modifier = Modifier.width(2.dp))

            // 일정 표시
            Text(
                text = if (schedule != null && (schedule.startDate.toLocalDate() == today || today.dayOfWeek == DayOfWeek.SUNDAY))
                    schedule.text else " ",
                maxLines = 1,
                modifier = Modifier.fillMaxWidth()
                    .background(color = if (schedule != null) Color(schedule.color) else Color.Transparent)
                    .padding(4.dp),
                overflow = TextOverflow.Ellipsis,
                fontSize = viewModel.design.value.textSize.dp(),
                color = if (schedule?.isHoliday == true) Color(viewModel.design.value.holidayTextColor) else Color.White
            )
        }

        // 일정 표시 간의 패딩
        Spacer(modifier = Modifier.height(2.dp))
    }
}

@Preview
@Composable
fun PreviewSchedule() {
    val viewModel = YearCalendarViewModel()
    ScheduleText(
        today = LocalDate.now(),
        weekScheduleList = Array(7) {
            Array(viewModel.design.value.visibleScheduleCount) {
                CalendarScheduleObject(
                    id = 1,
                    color = TimeUtils.getColorInt(255, 0, 0),
                    text = "test",
                    startDate = LocalDateTime.now(),
                    endDate = LocalDateTime.now()
                )
            }
        },
        viewModel = viewModel
    )
}
